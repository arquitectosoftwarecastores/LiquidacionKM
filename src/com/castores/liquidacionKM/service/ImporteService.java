package com.castores.liquidacionKM.service;

import com.castores.liquidacionKM.CustomDao.DepositoCustomDao;
import com.castores.liquidacionKM.CustomDao.DetKilometrosViajesCustomDao;
import com.castores.liquidacionKM.CustomDao.GuiaCustomDao;
import com.castores.liquidacionKM.CustomDao.LiquidacionCustomDao;
import com.castores.liquidacionKM.CustomDao.QuimicoCustomDao;
import Injector.AppInjector;
import castores.core.ModelMap;
import castores.dao.camiones.Porcentaje_liquida_camioneta_repartoDao;
import castores.dao.castores.ParametrosDao;
import castores.model.camiones.Depositos;
import castores.model.camiones.LiquidaMA;
import castores.model.camiones.Liquidaciones;
import castores.model.camiones.Porcentaje_liquida_camioneta_reparto;
import castores.model.camiones.Quimicos;
import castores.model.camiones.Tipounidad;
import castores.model.castores.Parametros;
import castores.model.talones.Det_kilometros_viajes;
import castores.model.talones.Viajes;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.castores.datautilsapi.log.LoggerUtils;
import com.castores.liquidacionKM.CustomDao.TipoCambioDao;
import com.castores.liquidacionKM.CustomDao.TipoUnidadCustomDao;
import com.castores.liquidacionKM.CustomDao.ViajeCustomDao;
import com.castores.liquidacionKM.model.GuMACustom;
import com.castores.liquidacionKM.model.GuiaCustom;
import com.google.inject.Injector;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import liquidacionkm.TGlobales;

public class ImporteService {

    private List<List<Depositos>> lstLstDeposito;
    private List<Map<String, Object>> lstDiferencias;
    private List<Double> lstTotalDepositoGuia;
    private final int[] arrGuiaDolar = new int[]{2, 8, 12, 21, 23};
    private List<Double> lstPorcentajeCamionetaReparto;
    private List<Double> lstValorCamionetaReparto;
    private Map<String, Float> totales;

    public List<GuMACustom> getGuiasSinLiquidarByUnidad(int unidad, Connection conexion) {
        Injector inj = AppInjector.getInjector();

        GuiaCustomDao guiaCustomDao = (GuiaCustomDao) inj.getInstance(GuiaCustomDao.class);

        int contador = 1;

        String sql = "select * from talones.guias g inner join talones.importeguias ig on g.no_guia = ig.no_guia left join talones.importeguiascircuitos tim on tim.no_guia = ig.no_guia ";
        String sqlCondicion = "where g.unidad='" + unidad + "' and ig.liquidable=" + 1 + " and g.status>" + 0 + " and g.anioliq is null ";

        List<ModelMap> lstGuia = guiaCustomDao.findByEspecial(sql + sqlCondicion + ";", conexion, contador);
        return getGuiasSinLiquidar(lstGuia, conexion);
    }

    private Object[] getTablas(Date fechaInicial, Date fechaFinal) {
        List<String> lstTabla = new ArrayList<String>();
        int anioInicial = fechaInicial.getYear();
        int anioFinal = fechaFinal.getYear();
        int mesInicial = fechaInicial.getMonth() + 1;
        int mesFinal = fechaFinal.getMonth() + 1;
        if (anioInicial < 1900) {
            anioInicial += 1900;
        }
        if (anioFinal < 1900) {
            anioFinal += 1900;
        }
        for (int anio = anioInicial; anio <= anioFinal; anio++) {
            int mes = 1;
            int mesLimite = 12;
            if (anio == anioInicial) {
                mes = mesInicial;
            }
            if (anio == anioFinal) {
                mesLimite = mesFinal;
            }
            for (; mes <= mesLimite; mes++) {
                lstTabla.add("" + mes + anio);
            }
        }
        return lstTabla.toArray();
    }

    private List<GuMACustom> getGuiasSinLiquidar(List<ModelMap> lstGuias, Connection conexion) {
        List<GuMACustom> guiasSinLiquidar = new ArrayList<GuMACustom>();
        for (ModelMap guia : lstGuias) {
            GuMACustom gu = ((GuiaCustom) guia.getModel("g")).getObjGuMA1(conexion);
            if (gu != null) {
                guiasSinLiquidar.add(gu);
            }
        }

        Collections.sort(guiasSinLiquidar, new Comparator<GuMACustom>() {
            @Override
            public int compare(GuMACustom guMACustomPrimero, GuMACustom guMACustomSegundo) {
                if (guMACustomPrimero.getFecha().before(guMACustomSegundo.getFecha())) {
                    return -1;
                }
                if (guMACustomPrimero.getFecha().after(guMACustomSegundo.getFecha())) {
                    return 1;
                }
                return guMACustomPrimero.getNoGuia().compareTo(guMACustomSegundo.getNoGuia());
            }
        });
        return guiasSinLiquidar;
    }

    public void procesaGuias(List<GuMACustom> lstGuia, boolean conDepositos, boolean soloGuiasSinLiquidar, Connection conexion13, Connection conexion23) {
        this.lstLstDeposito = new ArrayList<List<Depositos>>();
        this.lstDiferencias = new ArrayList<Map<String, Object>>();
        this.lstTotalDepositoGuia = new ArrayList<Double>();
        this.lstPorcentajeCamionetaReparto = new ArrayList<Double>();
        this.lstValorCamionetaReparto = new ArrayList<Double>();
        this.totales = new HashMap<String, Float>();

        float totalDeposito = 0.0F;
        float totalDiferencia = 0.0F;
        float totalFlete = 0.0F;
        float totalFleteOperador = 0.0F;
        float totalCPD = 0.0F;
        float total = 0.0F;

        float totalFleteSinRecoleccion = 0.0F;
        float totalSinRecoleccion = 0.0F;
        float totalFleteOperadorSinRecoleccion = 0.0F;
        float totalCPDSinRecoleccion = 0.0F;

        float totalParaLiquidacion = 0.0F;
        double tipoCambio = new TipoCambioDao().getTipoCambio();
        for (GuMACustom gu : lstGuia) {
            double tipoCambioImporte = 1.0D;
            for (int i = 0; i < this.arrGuiaDolar.length; i++) {
                if (gu.getMoneda() == this.arrGuiaDolar[i]) {
                    tipoCambioImporte = tipoCambio;
                    break;
                }
            }
            if (gu.getMoneda() != 4) {
                if (gu.getObjImporteguias(conexion23) != null) {
                    totalFleteSinRecoleccion = (float) (totalFleteSinRecoleccion + gu.getObjImporteguias(conexion23).getTotalflete() * tipoCambioImporte);
                    totalSinRecoleccion = (float) (totalSinRecoleccion + gu.getObjImporteguias(conexion23).getTotaltotal() * tipoCambioImporte);
                }
                if (gu.getObjImporteguiascircuitos(conexion23) != null) {
                    totalFleteOperadorSinRecoleccion = (float) (totalFleteOperadorSinRecoleccion + gu.getObjImporteguiascircuitos(conexion23).getFlete_operador() * tipoCambioImporte);
                    totalCPDSinRecoleccion = (float) (totalCPDSinRecoleccion + gu.getObjImporteguiascircuitos(conexion23).getTotalcdp() * tipoCambioImporte);
                    totalFleteOperador = (float) (totalFleteOperador + gu.getObjImporteguiascircuitos(conexion23).getFlete_operador() * tipoCambioImporte);
                    totalCPD = (float) (totalCPD + gu.getObjImporteguiascircuitos(conexion23).getTotalcdp() * tipoCambioImporte);
                    totalParaLiquidacion = (float) (totalParaLiquidacion + gu.getObjImporteguiascircuitos(conexion23).getTotaltotal() * tipoCambioImporte);
                }
            }
            if (gu.getObjImporteguias(conexion23) != null) {
                totalFlete = (float) (totalFlete + gu.getObjImporteguias(conexion23).getTotalflete() * tipoCambioImporte);
                total = (float) (total + gu.getObjImporteguias(conexion23).getTotaltotal() * tipoCambioImporte);
            }

            if (conDepositos) {
                double porcentajeReparto = getPorcentajeReparto(gu, conexion23);
                double valorReparto = porcentajeReparto;
                if (valorReparto == -1.0D) {
                    valorReparto = getValorReparto(gu, conexion23);
                }
                List<Depositos> lstDeposito = getDepositos(gu, soloGuiasSinLiquidar, conexion13);
                double totalDepositoTMP = 0.0D;
                for (Depositos deposito : lstDeposito) {
                    totalDepositoTMP += deposito.getObjDepMA().getTotal();
                }
                this.lstLstDeposito.add(lstDeposito);
                this.lstTotalDepositoGuia.add(totalDepositoTMP);
                this.lstPorcentajeCamionetaReparto.add(porcentajeReparto);
                this.lstValorCamionetaReparto.add(valorReparto);

                double diferencia = totalDepositoTMP - valorReparto;
                totalDeposito = (float) (totalDeposito + totalDepositoTMP);
                totalDiferencia = (float) (totalDiferencia + diferencia);
                if (diferencia != 0.0D) {
                    Map<String, Object> objDiferencia = new HashMap<String, Object>();
                    objDiferencia.put("gu", gu);
                    objDiferencia.put("importe_guia", totalFlete);
                    objDiferencia.put("importe_dep", totalDepositoTMP);
                    objDiferencia.put("importe_diff", diferencia);
                    this.lstDiferencias.add(objDiferencia);
                }
            }
        }
        this.totales.put("totaldepositos", totalDeposito);
        this.totales.put("totaldiferencias", totalDiferencia);
        this.totales.put("totalflete", totalFlete);
        this.totales.put("totalfleteop", totalFleteOperador);
        this.totales.put("totalcpd", totalCPD);
        this.totales.put("total", total);

        this.totales.put("totalfletesinrecoleccion", totalFleteSinRecoleccion);
        this.totales.put("totalsinrecoleccion", totalSinRecoleccion);
        this.totales.put("totalfleteopsinrecoleccion", totalFleteOperadorSinRecoleccion);
        this.totales.put("totalcpdsinrecoleccion", totalCPDSinRecoleccion);
        this.totales.put("totalparaliquidacion", totalParaLiquidacion);
    }

    private double getValorReparto(GuMACustom guMACustom, Connection conexion) {
        if (guMACustom.getMoneda() != 4) {
            if (guMACustom.getObjImporteguias(conexion) != null) {
                return guMACustom.getObjImporteguias(conexion).getTotaltotal();
            }
            return 0.0D;
        }
        return 0.0D;
    }

    private double getPorcentajeReparto(GuMACustom guMACustom, Connection conexion) {
        if (guMACustom.getMoneda() == 5) {
            Injector injector = AppInjector.getInjector();
            Porcentaje_liquida_camioneta_repartoDao porcentajeLiquidaCamionetaRepartoDao = (Porcentaje_liquida_camioneta_repartoDao) injector.getInstance(Porcentaje_liquida_camioneta_repartoDao.class);
            CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
            criteriaBuilder.eq("idoficina", guMACustom.getIdOficina());
            criteriaBuilder.lte("fechaaplicacion", guMACustom.getFecha());
            criteriaBuilder.orderDesc("fechaaplicacion");
            criteriaBuilder.setlimit(1L);
            List<Porcentaje_liquida_camioneta_reparto> lstPorcentajeLiquidaCamionetaReparto = porcentajeLiquidaCamionetaRepartoDao.findBy(criteriaBuilder);
            if (!lstPorcentajeLiquidaCamionetaReparto.isEmpty() && ((Porcentaje_liquida_camioneta_reparto) lstPorcentajeLiquidaCamionetaReparto.get(0)).getPorcentajeaplicar() > 0.0D) {
                return guMACustom.getObjImporteguias(conexion).getTotalflete() * ((Porcentaje_liquida_camioneta_reparto) lstPorcentajeLiquidaCamionetaReparto.get(0)).getPorcentajeaplicar() / 100.0D;
            }
        }
        return -1.0D;
    }

    private List<Depositos> getDepositos(GuMACustom guMACustom, boolean soloGuiasSinLiquidar, Connection conexion) {
        Injector injector = AppInjector.getInjector();
        DepositoCustomDao depositoCustomDao = (DepositoCustomDao) injector.getInstance(DepositoCustomDao.class);
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        criteriaBuilder.eq("unidad", guMACustom.getUnidad());
        criteriaBuilder.eq("no_guia", guMACustom.getNoGuia());
        if (soloGuiasSinLiquidar) {
            criteriaBuilder.isNull("noliq");
        }
        return depositoCustomDao.findBy(criteriaBuilder, conexion);
    }

    public double getFleteOptimo(int idTipoUnidad, int diasTranscurridos, int idUnidad, String noEconomico, Connection conexion) {
        Injector inj = AppInjector.getInjector();
        TipoUnidadCustomDao tipoUnidadCustomDao = (TipoUnidadCustomDao) inj.getInstance(TipoUnidadCustomDao.class);
        Tipounidad tipoUnidad = tipoUnidadCustomDao.find(new Tipounidad(idTipoUnidad), conexion);
        if (tipoUnidad == null) {
            return 0.0D;
        }
        double fleteOptimo = tipoUnidad.getRendimientomensual() / 30.4D * diasTranscurridos;

        QuimicoCustomDao quimicoCustomDao = (QuimicoCustomDao) inj.getInstance(QuimicoCustomDao.class);
        Quimicos quimico = quimicoCustomDao.find(new Quimicos(Integer.parseInt(noEconomico)), conexion);
        if (quimico != null && quimico.getStatus() == 1) {

            int idParametro = 9;
            if (idTipoUnidad == 2) {
                idParametro = 10;
            }
            double parametro = Double.parseDouble(getParametroCastores(idParametro, 4));

            double incremento = fleteOptimo * parametro / 100.0D;
            fleteOptimo += incremento;
        }
        return fleteOptimo;
    }

    public String getParametroCastores(int idParametro, int idPrograma) {
        Injector inj = AppInjector.getInjector();
        ParametrosDao dao = (ParametrosDao) inj.getInstance(ParametrosDao.class);
        CriteriaBuilder cb = new CriteriaBuilder();
        cb.eq("idparametro", idParametro);
        cb.eq("idprograma", idPrograma);
        List<Parametros> lstParametro = dao.findBy(cb);
        if (lstParametro.isEmpty()) {
            return "";
        }
        return ((Parametros) lstParametro.get(0)).getValor();
    }

    public double getFleteMovido() {
        return ((this.totales.get("totalfleteop")) + (this.totales.get("totalcpd")));
    }

    public Date getFechaUltimaLiq(int unidad, Connection conexion) {
        Injector injector = AppInjector.getInjector();
        LiquidacionCustomDao liquidacionCustomDao = (LiquidacionCustomDao) injector.getInstance(LiquidacionCustomDao.class);
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        criteriaBuilder.eq("unidad", unidad);
        criteriaBuilder.orderDesc("idpreliquidacion");
        criteriaBuilder.setlimit(1L);
        List<Liquidaciones> lstLiquidaciones = liquidacionCustomDao.findBy(criteriaBuilder, conexion);
        if (lstLiquidaciones.isEmpty() || ((Liquidaciones) lstLiquidaciones.get(0)).getObjLiquidaMA() == null) {
            return null;
        }
        LiquidaMA liquidaMA = ((Liquidaciones) lstLiquidaciones.get(0)).getObjLiquidaMA();
        if (liquidaMA.getFechainicio() != null) {
            return liquidaMA.getFechainicio();
        }
        return liquidaMA.getFechafin();
    }

    public double getKilometros(Date fechaUltimaLiq, int idUnidad, Connection conexion) {
        try {
            String fechaInicial = TGlobales.verFecha(fechaUltimaLiq, "yyyy-MM-dd");
            String fechaFinal = TGlobales.verFecha(new Date(), "yyyy-MM-dd");
            double KM = 0.0D;
            Injector injector = AppInjector.getInjector();
            ViajeCustomDao viajeCustomDao = (ViajeCustomDao) injector.getInstance(ViajeCustomDao.class);
            CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
            criteriaBuilder.eq("idunidad", idUnidad);
            criteriaBuilder.gte("fechaviaje", fechaInicial);
            criteriaBuilder.lt("fechaviaje", fechaFinal);
            criteriaBuilder.orderAsc("fechaviaje");

            List<Viajes> lstViaje = viajeCustomDao.findBy(criteriaBuilder, conexion);

            for (Viajes viaje : lstViaje) {
                DetKilometrosViajesCustomDao daoViaje = (DetKilometrosViajesCustomDao) injector.getInstance(DetKilometrosViajesCustomDao.class);

                CriteriaBuilder cbViaje = new CriteriaBuilder();
                cbViaje.eq("idviaje", viaje.getIdviaje());
                cbViaje.eq("idoficina", viaje.getIdoficina());

                List<Det_kilometros_viajes> lstKmViaje = daoViaje.findBy(cbViaje, conexion);
                System.out.println(daoViaje.getLastQuery());
                for (Det_kilometros_viajes detKilometrosViaje : lstKmViaje) {
                    KM += detKilometrosViaje.getKilometros();
                }
            }

            return 0.0D;
        } catch (Exception excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, excepcion.getLocalizedMessage(), Thread.currentThread().getStackTrace());
            return 0.0D;
        }
    }

    public void limpiavariables() {
        this.lstLstDeposito = null;
        this.lstDiferencias = null;
        this.lstTotalDepositoGuia = null;
        this.lstPorcentajeCamionetaReparto = null;
        this.lstValorCamionetaReparto = null;
        this.totales = null;
    }
}
