package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.core.PreparedParams;
import castores.dao.talones.ImporteguiascircuitosDao;
import castores.model.talones.Importeguiascircuitos;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class ImporteGuiaCircuitoCustomDao
        extends ImporteguiascircuitosDao {

    @Inject
    public ImporteGuiaCircuitoCustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
    }

    public Importeguiascircuitos find(Importeguiascircuitos importeGuiaCircuito, Connection conexion) {
        String FIND_BY_ID_IGC = "SELECT * FROM talones.importeguiascircuitos WHERE no_guia = ? ";
        this.lastQuery = "";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conexion.prepareStatement(FIND_BY_ID_IGC);
            PreparedParams preparedParams = new PreparedParams(preparedStatement);
            int contador = 1;
            preparedParams.setString(contador++, importeGuiaCircuito.getNo_guia());
            this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_IGC, preparedParams.getParamLIst());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                importeGuiaCircuito = getEntityFromRS(resultSet);
                return importeGuiaCircuito;
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
