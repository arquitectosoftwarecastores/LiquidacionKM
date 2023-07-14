package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.talones.GuMADao;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.castores.liquidacionKM.model.GuMACustom;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class GuMACustomDao
        extends GuMADao {

    @Inject
    public GuMACustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
    }

    public GuMACustom find(GuMACustom guMACustom, Connection conexion) {
        String FIND_BY_ID_GMA = "SELECT * FROM talones.gu#&tabla&# WHERE no_guia = ? ";
        if (this.tabla == null) {
            throw new RuntimeException("Debe especificar el mes y año de la consulta usando el metodo setTaba()");
        }
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(FIND_BY_ID_GMA.replaceFirst("#&tabla&#", this.tabla));
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setString(contador++, guMACustom.getNoGuia());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_GMA.replaceFirst("#&tabla&#", this.tabla), preparedParams.getParamLIst());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                guMACustom = getEntityFromRS1(resultSet);
                return guMACustom;
            }
            return null;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }

    public GuMACustom getEntityFromRS1(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            GuMACustom guMACustom = new GuMACustom();
            guMACustom.setNoGuia(resultSet.getString("no_guia"));
            guMACustom.setUnidad(resultSet.getString("unidad"));
            guMACustom.setPlacas(resultSet.getString("placas"));
            guMACustom.setIdOperador(resultSet.getInt("idoperador"));
            guMACustom.setRemolque(resultSet.getInt("remolque"));
            guMACustom.setOrigen(resultSet.getInt("origen"));
            guMACustom.setDestino(resultSet.getInt("destino"));
            guMACustom.setDespacho(resultSet.getString("despacho"));
            guMACustom.setIdPersonal(resultSet.getInt("idpersonal"));
            guMACustom.setIdOficina(resultSet.getString("idoficina"));
            guMACustom.setMoneda(resultSet.getInt("moneda"));
            guMACustom.setConversion(resultSet.getDouble("conversion"));
            guMACustom.setFecha(resultSet.getDate("fecha"));
            guMACustom.setHora(resultSet.getTime("hora"));
            guMACustom.setStatus(resultSet.getInt("status"));
            guMACustom.setIdCliente(resultSet.getInt("idcliente"));
            guMACustom.setIdProducto(resultSet.getInt("idproducto"));
            guMACustom.setCita(resultSet.getTimestamp("cita"));
            guMACustom.setTipoUnidad(resultSet.getInt("tipounidad"));
            return guMACustom;
        }
        return null;
    }
}
