package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.dao.talones.Det_kilometros_viajesDao;
import castores.model.talones.Det_kilometros_viajes;
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

public class DetKilometrosViajesCustomDao
        extends Det_kilometros_viajesDao {

    @Inject
    public DetKilometrosViajesCustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
    }

    public List<Det_kilometros_viajes> findBy(CriteriaBuilder criteriaBuilder, Connection conexion) {
        String FIND_BY_BASE_DV = "SELECT * FROM talones.det_kilometros_viajes";
        this.lastQuery = "";

        List<Det_kilometros_viajes> lstKilometrosViajes = new ArrayList<Det_kilometros_viajes>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_DV);
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_DV + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_DV + criteriaBuilder.getPreparedCriteria());
            }

            int contador = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object value : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(contador++, value);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_BASE_DV + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = FIND_BY_BASE_DV + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = FIND_BY_BASE_DV;
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Det_kilometros_viajes kilometrosViajes = getEntityFromRS(resultSet);
                if (kilometrosViajes != null) {
                    lstKilometrosViajes.add(kilometrosViajes);
                }
            }
            return lstKilometrosViajes;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }
}
