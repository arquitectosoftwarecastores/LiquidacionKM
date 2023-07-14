package com.castores.liquidacionKM.service;

import Injector.AppInjector;
import castores.core.JoinBuilder;
import castores.core.ModelMap;
import castores.dao.camiones.BitacoraDao;
import castores.dao.camiones.CamionesDao;
import castores.model.camiones.Bitacora;
import castores.model.camiones.Camiones;
import castores.model.camiones.Liquidacion_km;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.castores.datautilsapi.log.LoggerUtils;
import com.castores.liquidacionKM.CustomDao.CamionCustomDao;
import com.castores.liquidacionKM.CustomDao.GuiaCustomDao;
import com.castores.liquidacionKM.CustomDao.LiquidacionKMCustomDao;
import com.castores.liquidacionKM.model.GuMACustom;
import com.google.inject.Injector;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class LiquidacionKMService {

    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.println("LiquidacionKM ver. 13 - 27-05-2016 By Ing. Fernando Gomez.");
            return;
        }
        System.out.println("LiquidacionKM ver. 9 - 27-05-2016 By Ing. Fernando Gomez.");
        getFletes();
    }

    private static void getFletes() {
        Date fechaActual = getFechaActual();
        Injector injector = AppInjector.getInjector();
        int contador = 0;
        int temporizadorConexiones = 0;

        List<ModelMap> lstPendiente = new ArrayList<ModelMap>();
        List<ModelMap> lstUnidadesActivas = getUnidadesActivas();
        ImporteService importeService = new ImporteService();
        CamionCustomDao camionCustomDao = (CamionCustomDao) injector.getInstance(CamionCustomDao.class);
        GuiaCustomDao guiaCustomDao = (GuiaCustomDao) injector.getInstance(GuiaCustomDao.class);
        Connection conexion13 = camionCustomDao.conectar();
        Connection conexion23 = guiaCustomDao.conectar();
        try {
            for (ModelMap unidadActiva : lstUnidadesActivas) {
                Camiones camiones = (Camiones) unidadActiva.getModel("c");
                Bitacora bitacora = (Bitacora) unidadActiva.getModel("b");

                contador++;
                temporizadorConexiones++;
                System.out.println(contador + ".  unidad: " + camiones.getUnidad() + ". No.Eco: " + camiones.getNoeconomico());
                try {
                    if (bitacora != null) {
                        Date fechaLiquidacion = importeService.getFechaUltimaLiq(camiones.getUnidad(), conexion13);
                        if (fechaLiquidacion == null) {
                            fechaLiquidacion = new Date();
                        }
                        int diasLiquidacion = (int) (((new Date()).getTime() - fechaLiquidacion.getTime()) / 86400000L);

                        List<GuMACustom> lstGuiaMA = importeService.getGuiasSinLiquidarByUnidad(camiones.getUnidad(), conexion23);
                        importeService.procesaGuias(lstGuiaMA, false, true, conexion13, conexion23);

                        double fleteOptimo = importeService.getFleteOptimo(camiones.getIdtipounidad(), diasLiquidacion, camiones.getUnidad(), camiones.getNoeconomico(), conexion13);
                        double fleteMovido = importeService.getFleteMovido();
                        double KM = importeService.getKilometros(fechaLiquidacion, camiones.getUnidad(), conexion23);
                        double diferencia = fleteMovido - fleteOptimo;
                        double ventaKM = 0.0D;
                        if (KM > 0.0D) {
                            ventaKM = fleteMovido / KM;
                        }
                        setLiquidacionKilometraje(camiones, bitacora, fechaLiquidacion, fleteMovido, fechaActual, fleteOptimo, KM, diferencia, diasLiquidacion, ventaKM, conexion13);
                        importeService.limpiavariables();
                        if (temporizadorConexiones >= 1000) {
                            camionCustomDao.desconectar(conexion13);
                            guiaCustomDao.desconectar(conexion23);
                            conexion13 = camionCustomDao.conectar();
                            conexion23 = guiaCustomDao.conectar();
                            temporizadorConexiones = 0;
                        }
                    }
                } catch (Exception excepcion) {
                    LoggerUtils.printLog(LiquidacionKMService.class, Level.SEVERE, excepcion, excepcion.getLocalizedMessage(), Thread.currentThread().getStackTrace());
                }
            }
        } catch (Exception excepcion) {
            LoggerUtils.printLog(LiquidacionKMService.class, Level.SEVERE, excepcion, excepcion.getLocalizedMessage(), Thread.currentThread().getStackTrace());
        } finally {
            camionCustomDao.desconectar(conexion13);
            guiaCustomDao.desconectar(conexion23);
        }
    }

    private static Date getFechaActual() {
        Injector injector = AppInjector.getInjector();
        CamionesDao camionesDao = (CamionesDao) injector.getInstance(CamionesDao.class);
        return camionesDao.getCurrentDate();
    }

    private static List<ModelMap> getUnidadesActivas() {
        Injector injector = AppInjector.getInjector();
        CamionesDao camionesDao = (CamionesDao) injector.getInstance(CamionesDao.class);
        JoinBuilder joinBuilder = new JoinBuilder("camiones.camiones c", CamionesDao.class);
        joinBuilder.ljoin("camiones.bitacora b on c.unidad = b.idunidad", BitacoraDao.class);
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        criteriaBuilder.gt("c.status", 0);

        criteriaBuilder.orderAsc("c.unidad");
        List<ModelMap> lstCamiones = camionesDao.findBy(criteriaBuilder, joinBuilder);
        return lstCamiones;
    }

    private static void setLiquidacionKilometraje(Camiones camiones, Bitacora bitacora, Date fechaLiq, double fleteMovido, Date fechaActual, Double fleteOptimo, double kmUnidad, double diferencia, int diasSinliquidar, double ventaKM, Connection conexion) {
        Injector injector = AppInjector.getInjector();
        LiquidacionKMCustomDao liquidacionKMCustomDao = (LiquidacionKMCustomDao) injector.getInstance(LiquidacionKMCustomDao.class);
        Liquidacion_km liquidacionKM = liquidacionKMCustomDao.find(new Liquidacion_km(camiones.getNoeconomico()), conexion);
        if (liquidacionKM != null) {
            CamionCustomDao camionCustomDao = (CamionCustomDao) injector.getInstance(CamionCustomDao.class);
            CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
            criteriaBuilder.eq("unidad", liquidacionKM.getIdunidad());
            criteriaBuilder.eq("idtipounidad", liquidacionKM.getIdtipounidad());
            List<Camiones> lstCamiones = camionCustomDao.findBy(criteriaBuilder, conexion);
            if (!lstCamiones.isEmpty() && ((Camiones) lstCamiones.get(0)).getStatus() != 1) {
                liquidacionKMCustomDao.remove(liquidacionKM, conexion);
                liquidacionKM = null;
            }
        }
        if (liquidacionKM == null) {
            try {
                liquidacionKM = new Liquidacion_km();
                liquidacionKM.setNoeconomico(camiones.getNoeconomico());
                liquidacionKM.setIdunidad(camiones.getUnidad());
                liquidacionKM.setIdtipounidad(camiones.getIdtipounidad());
                liquidacionKM.setIdliquidacion(bitacora.getIdliquidacion());
                liquidacionKM.setFecha_liquidacion(fechaLiq);
                liquidacionKM.setKm_acumulado(kmUnidad);
                liquidacionKM.setFlete_acumulado(fleteMovido);
                liquidacionKM.setFecha_actualizacion(fechaActual);
                liquidacionKM.setFlete_optimo(fleteOptimo);
                liquidacionKM.setDiferencia(diferencia);
                liquidacionKM.setDiassinliquidar(diasSinliquidar);
                liquidacionKM.setVentakm(ventaKM);

                if (liquidacionKMCustomDao.create(liquidacionKM, conexion) == -1L) {
                    System.out.println("Excepcion al Insertar Liquidacion Km: " + camiones.getUnidad() + ". No.Eco: " + camiones.getNoeconomico() + " ");
                }

            } catch (Exception excepcion) {
                String mensaje = "Excepcion al tratar de Insertar Liquidacion Km: " + camiones.getUnidad() + ". No.Eco: " + camiones.getNoeconomico() + ". ";
                LoggerUtils.printLog(LiquidacionKMService.class, Level.SEVERE, excepcion, mensaje, Thread.currentThread().getStackTrace());
            }
        } else {
            try {
                liquidacionKM.setIdliquidacion(bitacora.getIdliquidacion());
                liquidacionKM.setFecha_liquidacion(fechaLiq);
                liquidacionKM.setKm_acumulado(kmUnidad);
                liquidacionKM.setFlete_acumulado(fleteMovido);
                liquidacionKM.setFecha_actualizacion(fechaActual);
                liquidacionKM.setFlete_optimo(fleteOptimo);
                liquidacionKM.setDiferencia(diferencia);
                liquidacionKM.setDiassinliquidar(diasSinliquidar);
                liquidacionKM.setVentakm(ventaKM);

                if (liquidacionKMCustomDao.edit(liquidacionKM, conexion) == -1) {
                    System.out.println("Excepcion al Editar Liquidacion Km: " + camiones.getUnidad() + ". No.Eco: " + camiones.getNoeconomico() + " ");
                }
            } catch (Exception excepcion) {
                String mensaje = "Excepcion al tratar de Actualizar Liquidacion Km: " + camiones.getUnidad() + ". No.Eco: " + camiones.getNoeconomico() + ".";
                LoggerUtils.printLog(LiquidacionKMService.class, Level.SEVERE, excepcion, mensaje, Thread.currentThread().getStackTrace());
            }
        }
    }
}
