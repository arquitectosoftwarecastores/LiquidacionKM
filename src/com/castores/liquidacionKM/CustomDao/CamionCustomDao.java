package com.castores.liquidacionKM.CustomDao;

import castores.core.JoinBuilder;
import castores.core.ModelMap;
import castores.core.Persistencia;
import castores.dao.camiones.CamionesDao;
import castores.model.camiones.Camiones;
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

public class CamionCustomDao
        extends CamionesDao {

    @Inject
    private CamionCustomDao(Persistencia persistencia, @Named("Server13") String server) {
        super(persistencia, server);
    }

    public Connection conectar() {
        return this.persistencia.connect(this.server);
    }

    public void desconectar(Connection conexion) {
        DBUtils.close(conexion);
    }

    public List<ModelMap> findByC(CriteriaBuilder criteriaBuilder, JoinBuilder joinBuilder, Connection conexion) {
        if (joinBuilder == null) {
            return new ArrayList<ModelMap>();
        }
        this.lastQuery = "";

        List<ModelMap> lstCamiones = new ArrayList<ModelMap>();
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
                    lstCamiones.add(modelMap);
                }
            }
            return lstCamiones;
        } catch (IllegalAccessException exception) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, exception, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (InstantiationException exception) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, exception, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } catch (SQLException exception) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, exception, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }

    public List<Camiones> findBy(CriteriaBuilder criteriaBuilder, Connection conexion) {
        String FIND_BY_BASE_L = "SELECT * FROM camiones.camiones";
        this.lastQuery = "";
        Camiones camion;
        List<Camiones> lstCamiones = new ArrayList<Camiones>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (criteriaBuilder == null) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_L);
            } else if (criteriaBuilder.getValuesList() != null && !criteriaBuilder.getValuesList().isEmpty()) {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_L + " WHERE " + criteriaBuilder.getPreparedCriteria());
            } else {
                preparedStatement = conexion.prepareStatement(FIND_BY_BASE_L + criteriaBuilder.getPreparedCriteria());
            }

            int contador = 1;
            if (criteriaBuilder != null) {
                if (criteriaBuilder.getValuesList() != null) {
                    for (Object value : criteriaBuilder.getValuesList()) {
                        preparedStatement.setObject(contador++, value);
                    }
                    this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_BASE_L + " WHERE " + criteriaBuilder.getPreparedCriteria(), criteriaBuilder.getValuesList());
                } else {
                    this.lastQuery = FIND_BY_BASE_L + criteriaBuilder.getPreparedCriteria();
                }
            } else {
                this.lastQuery = FIND_BY_BASE_L;
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                camion = getEntityFromRS(resultSet);
                if (camion != null) {
                    lstCamiones.add(camion);
                }
            }
            return lstCamiones;
        } catch (SQLException exception) {
            LoggerUtils.printLog(getClass(), Level.SEVERE, exception, this.lastQuery, Thread.currentThread().getStackTrace());
            return null;
        } finally {
            DBUtils.close(preparedStatement);
            DBUtils.close(resultSet);
        }
    }
}
