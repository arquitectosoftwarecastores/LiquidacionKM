package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.camiones.QuimicosDao;
import castores.model.camiones.Quimicos;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class QuimicoCustomDao
        extends QuimicosDao {

    @Inject
    public QuimicoCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public Quimicos find(Quimicos quimico, Connection conexion) {
        String FIND_BY_ID_Q = "SELECT * FROM camiones.quimicos WHERE noeconomico = ? ";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(FIND_BY_ID_Q);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setInt(contador++, quimico.getNoeconomico());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_Q, preparedParams.getParamLIst());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                quimico = getEntityFromRS(resultSet);
                return quimico;
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
