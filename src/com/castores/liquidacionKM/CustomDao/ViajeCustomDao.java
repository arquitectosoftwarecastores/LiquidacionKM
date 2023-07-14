package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.dao.talones.ViajesDao;
import castores.model.talones.Viajes;
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

public class ViajeCustomDao
        extends ViajesDao {

    @Inject
    public ViajeCustomDao(Persistencia persistencia, @Named("Server23") String server) {
        super(persistencia, server);
    }

    public List<Viajes> findBy(CriteriaBuilder criteriaBuilder, Connection conexion) {
        String FIND_BY_BASE_V = "SELECT * FROM talones.viajes";
        this.lastQuery = "";

        List<Viajes> lstViaje = new ArrayList<Viajes>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_V);
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_V + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_V + criteriaBuilder.getPreparedCriteria());
            }

            int contador = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object valor : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(contador++, valor);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_BASE_V + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = FIND_BY_BASE_V + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = FIND_BY_BASE_V;
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Viajes viaje = getEntityFromRS(resultSet);
                if (viaje != null) {
                    lstViaje.add(viaje);
                }
            }
            return lstViaje;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }
}
