 package com.castores.liquidacionKM.CustomDao;
 
 import castores.core.Persistencia;
 import castores.core.PreparedParams;
 import castores.dao.talones.ImporteguiasDao;
 import castores.model.talones.Importeguias;
 import com.castores.datautilsapi.db.DBUtils;
 import com.castores.datautilsapi.log.LoggerUtils;
 import com.google.inject.Inject;
 import com.google.inject.name.Named;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.logging.Level;
 
 public class ImporteGuiaCustomDao
   extends ImporteguiasDao {
   @Inject
   public ImporteGuiaCustomDao(Persistencia persistencia, @Named("Server23") String server) {
     super(persistencia, server);
   }
   
   public Importeguias find(Importeguias importeGuia, Connection conexion) {
     String FIND_BY_ID_IG = "SELECT * FROM talones.importeguias WHERE no_guia = ? ";
     this.lastQuery = "";
     PreparedStatement preparedStatement = null;
     ResultSet resultSet = null;
     try {
       preparedStatement = conexion.prepareStatement(FIND_BY_ID_IG);
       PreparedParams preparedParams = new PreparedParams(preparedStatement);
       int contador = 1;
       preparedParams.setString(contador++, importeGuia.getNo_guia());
       this.lastQuery = DBUtils.getQueryFromprepareStatement(FIND_BY_ID_IG, preparedParams.getParamLIst());
       resultSet = preparedStatement.executeQuery();
       if (resultSet.next()) {
         importeGuia = getEntityFromRS(resultSet);
         return importeGuia;
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