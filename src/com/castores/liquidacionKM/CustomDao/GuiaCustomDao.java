package com.castores.liquidacionKM.CustomDao;

import castores.core.GenericDao;
import castores.core.GenericModel;
import castores.core.InjectorContainer;
import castores.core.Join;
import castores.core.JoinBuilder;
import castores.core.ModelMap;
import castores.core.Persistencia;
import castores.dao.talones.GuiasDao;
import castores.model.talones.Guias;
import castores.model.talones.Guiaviaje;
import castores.model.talones.Importeguias;
import castores.model.talones.Importeguiascircuitos;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.castores.liquidacionKM.model.GuiaCustom;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class GuiaCustomDao extends GuiasDao {

    @Inject
    public GuiaCustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
    }

    public Connection conectar() {
        return this.persistencia.connect(this.server);
    }

    public void desconectar(Connection conexion) {
        DBUtils.close(conexion);
    }

    public List<ModelMap> findBy(CriteriaBuilder criteriaBuilder, JoinBuilder joinBuilder, Connection conexion) {
        if (joinBuilder == null) {
            return new ArrayList<ModelMap>();
        }
        this.lastQuery = "";

        List<ModelMap> lstGuias = new ArrayList<ModelMap>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(joinBuilder.toString());
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(joinBuilder.toString() + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(joinBuilder.toString() + criteriaBuilder.getPreparedCriteria());
            }

            int contador = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object value : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(contador++, value);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(joinBuilder.toString() + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = joinBuilder.toString() + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = joinBuilder.toString();
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ModelMap modelMap = getMapFromRS(resultSet, 1, joinBuilder.getJoinList().iterator(), null);
                if (modelMap != null) {
                    lstGuias.add(modelMap);
                }
            }
            return lstGuias;
        } catch (IllegalAccessException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (InstantiationException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }

    public List<ModelMap> findByEspecial(String consulta, Connection conexion, int indice) {
        if (consulta.isEmpty()) {
            return new ArrayList<ModelMap>();
        }
        this.lastQuery = "";

        List<ModelMap> lstGuias = new ArrayList<ModelMap>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(consulta);
            this.lastQuery = consulta;
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ModelMap guia = getMapFromRS1(resultSet, 1, indice);
                if (guia != null) {
                    lstGuias.add(guia);
                }
            }
            return lstGuias;
        } catch (IllegalAccessException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (InstantiationException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }

    public ModelMap getMapFromRS1(ResultSet resultSet, int index, Iterator<Join> iJoin, Join actual) throws SQLException, InstantiationException, IllegalAccessException {
        ModelMap modelMap = null;
        if (resultSet != null && iJoin != null) {
            String alias = null;
            if (actual != null) {
                alias = actual.getAlias();

            } else if (iJoin.hasNext()) {
                Join join = iJoin.next();
                alias = join.getAlias();
            }

            Guias guia = new Guias();
            guia.setNo_guia(resultSet.getString(alias + ".no_guia"));
            index++;
            guia.setTabla(resultSet.getString(alias + ".tabla"));
            index++;
            guia.setUnidad(resultSet.getString(alias + ".unidad"));
            index++;
            guia.setStatus(resultSet.getInt(alias + ".status"));
            index++;
            guia.setIdliquidacion(resultSet.getInt(alias + ".idliquidacion"));
            index++;
            guia.setAnioliq(resultSet.getInt(alias + ".anioliq"));
            index++;
            if (iJoin.hasNext()) {
                Join join = iJoin.next();
                if (join.getDao() == null) {
                    throw new RuntimeException("Clase dao no puede ser nula al armar un join, verificar JoinBuilder.");
                }
                GenericDao dao = (GenericDao) InjectorContainer.getInject().getInstance(join.getDao());
                modelMap = dao.getMapFromRS(resultSet, index, iJoin, join);
            } else {
                modelMap = new ModelMap(new HashMap<String, GenericModel>());
            }
            modelMap.addModel(alias, (GenericModel) guia);
        }
        return modelMap;
    }

    public ModelMap getMapFromRS1(ResultSet resultSet, int index, int contador) throws SQLException, InstantiationException, IllegalAccessException {
        ModelMap mapGuia = null;
        if (resultSet != null) {
            String alias = "g";
            String alias1 = "ig";
            String alias2 = "tim";
            String alias3 = "gv";

            GuiaCustom guiaCustom = new GuiaCustom();
            guiaCustom.setNo_guia(resultSet.getString(alias + ".no_guia"));
            index++;
            guiaCustom.setTabla(resultSet.getString(alias + ".tabla"));
            index++;
            guiaCustom.setUnidad(resultSet.getString(alias + ".unidad"));
            index++;
            guiaCustom.setStatus(resultSet.getInt(alias + ".status"));
            index++;
            guiaCustom.setIdliquidacion(resultSet.getInt(alias + ".idliquidacion"));
            index++;
            guiaCustom.setAnioliq(resultSet.getInt(alias + ".anioliq"));
            index++;

            Importeguias importeGuia = new Importeguias();
            importeGuia.setNo_guia(resultSet.getString(alias1 + ".no_guia"));
            index++;
            importeGuia.setTotalflete(resultSet.getDouble(alias1 + ".totalflete"));
            index++;
            importeGuia.setTotalseguro(resultSet.getDouble(alias1 + ".totalseguro"));
            index++;
            importeGuia.setTotalcapufe(resultSet.getDouble(alias1 + ".totalcapufe"));
            index++;
            importeGuia.setTotalrecoleccion(resultSet.getDouble(alias1 + ".totalrecoleccion"));
            index++;
            importeGuia.setTotalentrega(resultSet.getDouble(alias1 + ".totalentrega"));
            index++;
            importeGuia.setTotalmaniobras(resultSet.getDouble(alias1 + ".totalmaniobras"));
            index++;
            importeGuia.setTotalcdp(resultSet.getDouble(alias1 + ".totalcdp"));
            index++;
            importeGuia.setTotalferry(resultSet.getDouble(alias1 + ".totalferry"));
            index++;
            importeGuia.setTotaldescuento(resultSet.getDouble(alias1 + ".totaldescuento"));
            index++;
            importeGuia.setTotalolrvc(resultSet.getDouble(alias1 + ".totalolrvc"));
            index++;
            importeGuia.setTotalotros(resultSet.getDouble(alias1 + ".totalotros"));
            index++;
            importeGuia.setTotalgps(resultSet.getDouble(alias1 + ".totalgps"));
            index++;
            importeGuia.setTotalsubtotal(resultSet.getDouble(alias1 + ".totalsubtotal"));
            index++;
            importeGuia.setTotaliva(resultSet.getDouble(alias1 + ".totaliva"));
            index++;
            importeGuia.setTotalretencion(resultSet.getDouble(alias1 + ".totalretencion"));
            index++;
            importeGuia.setTotalotrasLineas(resultSet.getDouble(alias1 + ".totalotrasLineas"));
            index++;
            importeGuia.setTotaltotal(resultSet.getDouble(alias1 + ".totaltotal"));
            index++;
            importeGuia.setTotalvalordeclarado(resultSet.getDouble(alias1 + ".totalvalordeclarado"));
            index++;
            importeGuia.setTotalbultos(resultSet.getInt(alias1 + ".totalbultos"));
            index++;
            importeGuia.setTotalpeso(resultSet.getDouble(alias1 + ".totalpeso"));
            index++;
            importeGuia.setTotaldetalones(resultSet.getInt(alias1 + ".totaldetalones"));
            index++;
            importeGuia.setTotaldetalonesocurre(resultSet.getInt(alias1 + ".totaldetalonesocurre"));
            index++;
            importeGuia.setEscompleto(resultSet.getInt(alias1 + ".escompleto"));
            index++;
            importeGuia.setLiquidable(resultSet.getInt(alias1 + ".liquidable"));
            index++;
            importeGuia.setFlete_operador(resultSet.getDouble(alias1 + ".flete_operador"));
            index++;

            Importeguiascircuitos importeGuiaCircuito = new Importeguiascircuitos();
            importeGuiaCircuito.setNo_guia(resultSet.getString(alias2 + ".no_guia"));
            index++;
            importeGuiaCircuito.setTotalflete(resultSet.getDouble(alias2 + ".totalflete"));
            index++;
            importeGuiaCircuito.setTotalseguro(resultSet.getDouble(alias2 + ".totalseguro"));
            index++;
            importeGuiaCircuito.setTotalcapufe(resultSet.getDouble(alias2 + ".totalcapufe"));
            index++;
            importeGuiaCircuito.setTotalrecoleccion(resultSet.getDouble(alias2 + ".totalrecoleccion"));
            index++;
            importeGuiaCircuito.setTotalentrega(resultSet.getDouble(alias2 + ".totalentrega"));
            index++;
            importeGuiaCircuito.setTotalmaniobras(resultSet.getDouble(alias2 + ".totalmaniobras"));
            index++;
            importeGuiaCircuito.setTotalcdp(resultSet.getDouble(alias2 + ".totalcdp"));
            index++;
            importeGuiaCircuito.setTotalferry(resultSet.getDouble(alias2 + ".totalferry"));
            index++;
            importeGuiaCircuito.setTotaldescuento(resultSet.getDouble(alias2 + ".totaldescuento"));
            index++;
            importeGuiaCircuito.setTotalolrvc(resultSet.getDouble(alias2 + ".totalolrvc"));
            index++;
            importeGuiaCircuito.setTotalotros(resultSet.getDouble(alias2 + ".totalotros"));
            index++;
            importeGuiaCircuito.setTotalgps(resultSet.getDouble(alias2 + ".totalgps"));
            index++;
            importeGuiaCircuito.setTotalsubtotal(resultSet.getDouble(alias2 + ".totalsubtotal"));
            index++;
            importeGuiaCircuito.setTotaliva(resultSet.getDouble(alias2 + ".totaliva"));
            index++;
            importeGuiaCircuito.setTotalretencion(resultSet.getDouble(alias2 + ".totalretencion"));
            index++;
            importeGuiaCircuito.setTotalotrasLineas(resultSet.getDouble(alias2 + ".totalotrasLineas"));
            index++;
            importeGuiaCircuito.setTotaltotal(resultSet.getDouble(alias2 + ".totaltotal"));
            index++;
            importeGuiaCircuito.setTotalvalordeclarado(resultSet.getDouble(alias2 + ".totalvalordeclarado"));
            index++;
            importeGuiaCircuito.setTotalbultos(resultSet.getInt(alias2 + ".totalbultos"));
            index++;
            importeGuiaCircuito.setTotalpeso(resultSet.getDouble(alias2 + ".totalpeso"));
            index++;
            importeGuiaCircuito.setTotaldetalones(resultSet.getInt(alias2 + ".totaldetalones"));
            index++;
            importeGuiaCircuito.setTotaldetalonesocurre(resultSet.getInt(alias2 + ".totaldetalonesocurre"));
            index++;
            importeGuiaCircuito.setEscompleto(resultSet.getInt(alias2 + ".escompleto"));
            index++;
            importeGuiaCircuito.setLiquidable(resultSet.getInt(alias2 + ".liquidable"));
            index++;
            importeGuiaCircuito.setFlete_operador(resultSet.getDouble(alias2 + ".flete_operador"));
            index++;

            if (contador == 2) {
                Guiaviaje guiaViaje = new Guiaviaje();
                guiaViaje.setIdviaje(resultSet.getLong(alias3 + ".idviaje"));
                index++;
                guiaViaje.setIdoficina(resultSet.getString(alias3 + ".idoficina"));
                index++;
                guiaViaje.setNo_guia(resultSet.getString(alias3 + ".no_guia"));
                index++;
                guiaViaje.setIdoficinaguia(resultSet.getString(alias3 + ".idoficinaguia"));
                index++;
                guiaViaje.setEstatusguia(resultSet.getInt(alias3 + ".estatusguia"));
                index++;
                guiaViaje.setConsecutivo(resultSet.getInt(alias3 + ".consecutivo"));
                index++;
                guiaViaje.setImpresionpreguia(resultSet.getInt(alias3 + ".impresionpreguia"));
                index++;
                guiaViaje.setImpresionguia(resultSet.getInt(alias3 + ".impresionguia"));
                index++;
                guiaViaje.setEstatus(resultSet.getInt(alias3 + ".estatus"));
                index++;
                guiaViaje.setIdpersonal(resultSet.getInt(alias3 + ".idpersonal"));
                index++;
                guiaViaje.setFechamod(resultSet.getDate(alias3 + ".fechamod"));
                index++;
                guiaViaje.setHoramod(resultSet.getTime(alias3 + ".horamod"));
                index++;
                guiaViaje.setTotalguia(resultSet.getDouble(alias3 + ".totalguia"));
                index++;
                guiaViaje.setIdoficinadeposito(resultSet.getString(alias3 + ".idoficinadeposito"));
                index++;
                guiaViaje.setTotaldeposito(resultSet.getDouble(alias3 + ".totaldeposito"));
                index++;
                guiaViaje.setOperacionguia(resultSet.getInt(alias3 + ".operacionguia"));
                index++;
                guiaViaje.setIdoficinadestino(resultSet.getString(alias3 + ".idoficinadestino"));
                index++;
                guiaViaje.setVisitada(resultSet.getInt(alias3 + ".visitada"));
                index++;
            }
            mapGuia = new ModelMap(new HashMap<String, GenericModel>());
            mapGuia.addModel(alias, (GenericModel) guiaCustom);
            mapGuia.addModel(alias1, (GenericModel) importeGuia);
            mapGuia.addModel(alias2, (GenericModel) importeGuiaCircuito);
            if (contador == 2) {
                mapGuia.addModel(alias3, (GenericModel) importeGuiaCircuito);
            }
        }
        return mapGuia;
    }
}
