package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.camiones.Liquidacion_kmDao;
import castores.model.camiones.Liquidacion_km;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class LiquidacionKMCustomDao extends Liquidacion_kmDao {

    @Inject
    public LiquidacionKMCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public long create(Liquidacion_km liquidacionKM, Connection conexion) {
        String INSERT_LKM = "INSERT INTO camiones.liquidacion_km VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(INSERT_LKM);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setString(contador++, liquidacionKM.getNoeconomico());
            preparedParams.setInt(contador++, liquidacionKM.getIdunidad());
            preparedParams.setInt(contador++, liquidacionKM.getIdtipounidad());
            preparedParams.setInt(contador++, liquidacionKM.getIdliquidacion());
            preparedParams.setDate(contador++, (liquidacionKM.getFecha_liquidacion() != null) ? new Date(liquidacionKM.getFecha_liquidacion().getTime()) : null);
            preparedParams.setDouble(contador++, liquidacionKM.getKm_acumulado());
            preparedParams.setDouble(contador++, liquidacionKM.getFlete_acumulado());
            preparedParams.setDate(contador++, (liquidacionKM.getFecha_actualizacion() != null) ? new Date(liquidacionKM.getFecha_actualizacion().getTime()) : null);
            preparedParams.setDouble(contador++, liquidacionKM.getFlete_optimo());
            preparedParams.setDouble(contador++, liquidacionKM.getDiferencia());
            preparedParams.setDouble(contador++, liquidacionKM.getVentakm());
            preparedParams.setInt(contador++, liquidacionKM.getDiassinliquidar());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(INSERT_LKM, preparedParams.getParamLIst());
            long idLiquidacionKM = preparedStatement.executeUpdate();
            return idLiquidacionKM;
        } catch (SQLException exception) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, exception, this.lastQuery, Thread.currentThread().getStackTrace());
            return -1L;
        } finally {
            DBUtils.close(resultSet);
            DBUtils.close(preparedStatement);
        }
    }

    public int edit(Liquidacion_km liquidacionKM, Connection conexion) {
        String UPDATE_LKM = "UPDATE camiones.liquidacion_km SET idunidad = ?, idtipounidad = ?, idliquidacion = ?, fecha_liquidacion = ?, km_acumulado = ?, flete_acumulado = ?, fecha_actualizacion = ?, flete_optimo = ?, diferencia = ?, ventakm = ?, diassinliquidar = ? WHERE noeconomico = ?";
        this.lastQuery = "";
        Liquidacion_km liquidacionKMOriginal = find(liquidacionKM);
        if (liquidacionKMOriginal != null) {

            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = conexion.prepareStatement(UPDATE_LKM);
                PreparedParams preparedParams = new PreparedParams(preparedStatement);
                int contador = 1;
                preparedParams.setInt(contador++, liquidacionKM.getIdunidad());
                preparedParams.setInt(contador++, liquidacionKM.getIdtipounidad());
                preparedParams.setInt(contador++, liquidacionKM.getIdliquidacion());
                preparedParams.setDate(contador++, (liquidacionKM.getFecha_liquidacion() != null) ? new Date(liquidacionKM.getFecha_liquidacion().getTime()) : null);
                preparedParams.setDouble(contador++, liquidacionKM.getKm_acumulado());
                preparedParams.setDouble(contador++, liquidacionKM.getFlete_acumulado());
                preparedParams.setDate(contador++, (liquidacionKM.getFecha_actualizacion() != null) ? new Date(liquidacionKM.getFecha_actualizacion().getTime()) : null);
                preparedParams.setDouble(contador++, liquidacionKM.getFlete_optimo());
                preparedParams.setDouble(contador++, liquidacionKM.getDiferencia());
                preparedParams.setDouble(contador++, liquidacionKM.getVentakm());
                preparedParams.setInt(contador++, liquidacionKM.getDiassinliquidar());
                preparedParams.setString(contador++, liquidacionKM.getNoeconomico());
                this.lastQuery = DBUtils.getQueryFromprepareStatement(UPDATE_LKM, preparedParams.getParamLIst());
                int idLiquidacion = preparedStatement.executeUpdate();

                if (idLiquidacion >= 0) {
                    this.lastQuery = getShortUpdate(liquidacionKMOriginal, liquidacionKM);
                }
                return idLiquidacion;
            } catch (SQLException excepcion) {
                LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
                return -1;
            } finally {
                DBUtils.close(resultSet);
                DBUtils.close(preparedStatement);
            }
        }
        return -1;
    }

    public int remove(Liquidacion_km liquidacionKM, Connection conexion) {
        String DELETE_LKM = "DELETE FROM camiones.liquidacion_km WHERE noeconomico = ? ";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(DELETE_LKM);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setString(contador++, liquidacionKM.getNoeconomico());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(DELETE_LKM, preparedParams.getParamLIst());
            int idLiquidacion = preparedStatement.executeUpdate();
            return idLiquidacion;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return -1;
        } finally {
            DBUtils.close(resultSet);
            DBUtils.close(preparedStatement);
        }
    }

    public Liquidacion_km find(Liquidacion_km liquidacionKM, Connection conexion) {
        String FIND_BY_ID_LKM = "SELECT * FROM camiones.liquidacion_km WHERE noeconomico = ? ";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(FIND_BY_ID_LKM);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setString(contador++, liquidacionKM.getNoeconomico());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_LKM, preparedParams.getParamLIst());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                liquidacionKM = getEntityFromRS(resultSet);
                return liquidacionKM;
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

    private String getShortUpdate(Liquidacion_km liquidacionKMAntes, Liquidacion_km liquidacionKMDespues) throws SQLException {
        ArrayList<Object> lstParametro = new ArrayList();
        this.lastQuery = "UPDATE camiones.liquidacion_km SET ";
        if (!liquidacionKMAntes.getNoeconomico().equals(liquidacionKMDespues.getNoeconomico())) {
            this.lastQuery = this.lastQuery + "noeconomico = ?, ";
            lstParametro.add(liquidacionKMDespues.getNoeconomico());
        }
        if (liquidacionKMAntes.getIdunidad() != liquidacionKMDespues.getIdunidad()) {
            this.lastQuery = this.lastQuery + "idunidad = ?, ";
            lstParametro.add(liquidacionKMDespues.getIdunidad());
        }
        if (liquidacionKMAntes.getIdtipounidad() != liquidacionKMDespues.getIdtipounidad()) {
            this.lastQuery = this.lastQuery + "idtipounidad = ?, ";
            lstParametro.add(liquidacionKMDespues.getIdtipounidad());
        }
        if (liquidacionKMAntes.getIdliquidacion() != liquidacionKMDespues.getIdliquidacion()) {
            this.lastQuery = this.lastQuery + "idliquidacion = ?, ";
            lstParametro.add(liquidacionKMDespues.getIdliquidacion());
        }
        if (liquidacionKMAntes.getFecha_liquidacion() != null && !liquidacionKMAntes.getFecha_liquidacion().equals(liquidacionKMDespues.getFecha_liquidacion())) {
            this.lastQuery = this.lastQuery + "fecha_liquidacion = ?, ";
            lstParametro.add(liquidacionKMDespues.getFecha_liquidacion());
        } else if (liquidacionKMAntes.getFecha_liquidacion() != liquidacionKMDespues.getFecha_liquidacion() && (liquidacionKMAntes.getFecha_liquidacion() == null || liquidacionKMDespues.getFecha_liquidacion() == null)) {
            this.lastQuery = this.lastQuery + "fecha_liquidacion = ?, ";
            lstParametro.add(liquidacionKMDespues.getFecha_liquidacion());
        }
        if (liquidacionKMAntes.getKm_acumulado() != liquidacionKMDespues.getKm_acumulado()) {
            this.lastQuery = this.lastQuery + "km_acumulado = ?, ";
            lstParametro.add(liquidacionKMDespues.getKm_acumulado());
        }
        if (liquidacionKMAntes.getFlete_acumulado() != liquidacionKMDespues.getFlete_acumulado()) {
            this.lastQuery = this.lastQuery + "flete_acumulado = ?, ";
            lstParametro.add(liquidacionKMDespues.getFlete_acumulado());
        }
        if (liquidacionKMAntes.getFecha_actualizacion() != null && !liquidacionKMAntes.getFecha_actualizacion().equals(liquidacionKMDespues.getFecha_actualizacion())) {
            this.lastQuery = this.lastQuery + "fecha_actualizacion = ?, ";
            lstParametro.add(liquidacionKMDespues.getFecha_actualizacion());
        } else if (liquidacionKMAntes.getFecha_actualizacion() != liquidacionKMDespues.getFecha_actualizacion() && (liquidacionKMAntes.getFecha_actualizacion() == null || liquidacionKMDespues.getFecha_actualizacion() == null)) {
            this.lastQuery = this.lastQuery + "fecha_actualizacion = ?, ";
            lstParametro.add(liquidacionKMDespues.getFecha_actualizacion());
        }
        if (liquidacionKMAntes.getFlete_optimo() != liquidacionKMDespues.getFlete_optimo()) {
            this.lastQuery = this.lastQuery + "flete_optimo = ?, ";
            lstParametro.add(liquidacionKMDespues.getFlete_optimo());
        }
        if (liquidacionKMAntes.getDiferencia() != liquidacionKMDespues.getDiferencia()) {
            this.lastQuery = this.lastQuery + "diferencia = ?, ";
            lstParametro.add(liquidacionKMDespues.getDiferencia());
        }
        if (liquidacionKMAntes.getVentakm() != liquidacionKMDespues.getVentakm()) {
            this.lastQuery = this.lastQuery + "ventakm = ?, ";
            lstParametro.add(liquidacionKMDespues.getVentakm());
        }
        if (liquidacionKMAntes.getDiassinliquidar() != liquidacionKMDespues.getDiassinliquidar()) {
            this.lastQuery = this.lastQuery + "diassinliquidar = ?, ";
            lstParametro.add(liquidacionKMDespues.getDiassinliquidar());
        }
        if (!this.lastQuery.equals("UPDATE camiones.liquidacion_km SET ")) {
            this.lastQuery = this.lastQuery.substring(0, this.lastQuery.length() - 2);
            this.lastQuery = this.lastQuery + " WHERE noeconomico = ?";
            lstParametro.add(liquidacionKMDespues.getNoeconomico());
            return DBUtils.getQueryFromprepareStatement(this.lastQuery, lstParametro);
        }
        return "";
    }
}
