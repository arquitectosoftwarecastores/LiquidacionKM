package com.castores.liquidacionKM.CustomDao;

import castores.core.Persistencia;
import castores.dao.camiones.LiquidacionesDao;
import castores.model.camiones.Liquidaciones;
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

public class LiquidacionCustomDao
        extends LiquidacionesDao {

    @Inject
    public LiquidacionCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public Connection conectar() {
        return this.persistencia.connect(this.server);
    }

    public void desconectar(Connection conexion) {
        DBUtils.close(conexion);
    }

    public List<Liquidaciones> findBy(CriteriaBuilder criteriaBuilder, Connection conexion) {
        String FIND_BY_BASE_LIQ = "SELECT * FROM camiones.liquidaciones";
        this.lastQuery = "";

        List<Liquidaciones> lstLiquidacion = new ArrayList<Liquidaciones>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_LIQ);
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_LIQ + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_LIQ + criteriaBuilder.getPreparedCriteria());
            }

            int contador = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object value : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(contador++, value);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_BASE_LIQ + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = FIND_BY_BASE_LIQ + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = FIND_BY_BASE_LIQ;
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Liquidaciones liquidacion = getEntityFromRS(resultSet);
                if (liquidacion != null) {
                    lstLiquidacion.add(liquidacion);
                }
            }
            return lstLiquidacion;
        } catch (SQLException excepcion) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, excepcion, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }
}
