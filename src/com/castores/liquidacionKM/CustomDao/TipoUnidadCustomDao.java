package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.camiones.TipounidadDao;
import castores.model.camiones.Tipounidad;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class TipoUnidadCustomDao
        extends TipounidadDao {

    @Inject
    public TipoUnidadCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public Tipounidad find(Tipounidad tipoUnidad, Connection conexion) {
        String FIND_BY_ID_TU = "SELECT * FROM camiones.tipounidad WHERE idtipounidad = ? ";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(FIND_BY_ID_TU);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setInt(contador++, tipoUnidad.getIdtipounidad());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_TU, preparedParams.getParamLIst());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tipoUnidad = getEntityFromRS(resultSet);
                return tipoUnidad;
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
}
