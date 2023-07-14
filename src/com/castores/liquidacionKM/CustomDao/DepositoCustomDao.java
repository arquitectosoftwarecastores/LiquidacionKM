package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.dao.camiones.DepositosDao;
import castores.model.camiones.Depositos;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.castores.datautilsapi.db.DBUtils;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DepositoCustomDao
        extends DepositosDao {

    @Inject
    public DepositoCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public List<Depositos> findBy(CriteriaBuilder criteriaBuilder, Connection conexion) {
        String FIND_BY_BASE_D = "SELECT * FROM camiones.depositos";
        this.lastQuery = "";

        List<Depositos> lstDepositos = new ArrayList<Depositos>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_D);
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_D + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_D + criteriaBuilder.getPreparedCriteria());
            }

            int i = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object value : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(i++, value);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_BASE_D + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = FIND_BY_BASE_D + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = FIND_BY_BASE_D;
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Depositos depositos = getEntityFromRS(resultSet);
                if (depositos != null) {
                    lstDepositos.add(depositos);
                }
            }
            return lstDepositos;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }
}
