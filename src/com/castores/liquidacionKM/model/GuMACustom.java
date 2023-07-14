 package com.castores.liquidacionKM.model;
 
 import com.castores.liquidacionKM.CustomDao.ImporteGuiaCustomDao;
 import com.castores.liquidacionKM.CustomDao.ImporteGuiaCircuitoCustomDao;
 import castores.core.GenericModelMA;
 import castores.core.InjectorContainer;
 import castores.dao.camiones.CamionesDao;
 import castores.dao.camiones.CiudadesDao;
 import castores.dao.camiones.OperadoresDao;
 import castores.dao.personal.OficinasDao;
 import castores.model.camiones.Camiones;
 import castores.model.camiones.Ciudades;
 import castores.model.camiones.Operadores;
 import castores.model.personal.Oficinas;
 import castores.model.talones.Importeguias;
 import castores.model.talones.Importeguiascircuitos;
 import java.sql.Connection;
 import java.util.Date;
 
 public class GuMACustom implements GenericModelMA {
   private String noGuia;
   private String unidad;
   private String placas;
   private int idOperador;
   private int remolque;
   private int origen;
   private int destino;
   private String despacho;
   private int idPersonal;
   private String idOficina;
   private int moneda;
   private double conversion;
   private Date fecha;
   private Date hora;
   private int status;
   private int idCliente;
   private int idProducto;
   private Date cita;
   private int tipoUnidad;
   private Importeguias objImporteGuia;
   private Importeguiascircuitos objImporteGuiaCircuito;
   private Oficinas objOficina;
   private Camiones objCamion;
   private Operadores objOperador;
   private Ciudades objDestino;
   private Ciudades objOrigen;
   
   public GuMACustom() {}
   
   public GuMACustom(String noGuia) {
     this();
     this.noGuia = noGuia;
   }
   
   public void setNoGuia(String noGuia) {
     this.noGuia = noGuia;
   }
   
   public void setUnidad(String unidad) {
     this.unidad = unidad;
   }
   
   public void setPlacas(String placas) {
     this.placas = placas;
   }
   
   public void setIdOperador(int idOperador) {
     this.idOperador = idOperador;
   }
   
   public void setRemolque(int remolque) {
     this.remolque = remolque;
   }
   
   public void setOrigen(int origen) {
     this.origen = origen;
   }
   
   public void setDestino(int destino) {
     this.destino = destino;
   }
   
   public void setDespacho(String despacho) {
     this.despacho = despacho;
   }
   
   public void setIdPersonal(int idPersonal) {
     this.idPersonal = idPersonal;
   }
   
   public void setIdOficina(String idOficina) {
     this.idOficina = idOficina;
   }
   
   public void setMoneda(int moneda) {
     this.moneda = moneda;
   }
   
   public void setConversion(double conversion) {
     this.conversion = conversion;
   }
   
   public void setFecha(Date fecha) {
     this.fecha = fecha;
   }
   
   public void setHora(Date hora) {
     this.hora = hora;
   }
   
   public void setStatus(int status) {
     this.status = status;
   }
   
   public void setIdCliente(int idCliente) {
     this.idCliente = idCliente;
   }
   
   public void setIdProducto(int idProducto) {
     this.idProducto = idProducto;
   }
   
   public void setCita(Date cita) {
     this.cita = cita;
   }
   
   public void setTipoUnidad(int tipoUnidad) {
     this.tipoUnidad = tipoUnidad;
   }
   
   public String getNoGuia() {
     if (this.noGuia == null)
       return ""; 
     return this.noGuia;
   }
   
   public String getUnidad() {
     if (this.unidad == null)
       return ""; 
     return this.unidad;
   }
   
   public String getPlacas() {
     if (this.placas == null)
       return ""; 
     return this.placas;
   }
   
   public int getIdOperador() {
     return this.idOperador;
   }
   
   public int getRemolque() {
     return this.remolque;
   }
   
   public int getOrigen() {
     return this.origen;
   }
   
   public int getDestino() {
     return this.destino;
   }
   
   public String getDespacho() {
     if (this.despacho == null)
       return ""; 
     return this.despacho;
   }
   
   public int getIdPersonal() {
     return this.idPersonal;
   }
   
   public String getIdOficina() {
     if (this.idOficina == null)
       return ""; 
     return this.idOficina;
   }
   
   public int getMoneda() {
     return this.moneda;
   }
   
   public double getConversion() {
     return this.conversion;
   }
   
   public Date getFecha() {
     return this.fecha;
   }
   
   public Date getHora() {
     return this.hora;
   }
   
   public int getStatus() {
     return this.status;
   }
   
   public int getIdCliente() {
     return this.idCliente;
   }
   
   public int getIdProducto() {
     return this.idProducto;
   }
   
   public Date getCita() {
     return this.cita;
   }
   
   public int getTipoUnidad() {
     return this.tipoUnidad;
   }
 
 
 
   
   public Importeguias getObjImporteguias(boolean recargar, Connection conexion) {
     if (this.objImporteGuia == null || recargar) {
       ImporteGuiaCustomDao importeGuiaCustomDao = (ImporteGuiaCustomDao)InjectorContainer.getInject().getInstance(ImporteGuiaCustomDao.class);
       this.objImporteGuia = importeGuiaCustomDao.find(new Importeguias(this.noGuia), conexion);
     } 
     return this.objImporteGuia;
   }
   
   public Importeguias getObjImporteguias(Connection conexion) {
     return getObjImporteguias(false, conexion);
   }
 
 
 
   
   public Importeguiascircuitos getObjImporteguiascircuitos(boolean recargar, Connection conexion) {
     if (this.objImporteGuiaCircuito == null || recargar) {
       
       ImporteGuiaCircuitoCustomDao dao = (ImporteGuiaCircuitoCustomDao)InjectorContainer.getInject().getInstance(ImporteGuiaCircuitoCustomDao.class);
       this.objImporteGuiaCircuito = dao.find(new Importeguiascircuitos(this.noGuia), conexion);
     } 
     return this.objImporteGuiaCircuito;
   }
 
   
   public Importeguiascircuitos getObjImporteguiascircuitos(Connection conexion) {
     return getObjImporteguiascircuitos(false, conexion);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public Oficinas getObjOficinas(boolean recargar) {
     if (this.objOficina == null || recargar) {
       OficinasDao dao = (OficinasDao)InjectorContainer.getInject().getInstance(OficinasDao.class);
       this.objOficina = dao.find(new Oficinas(this.idOficina));
     } 
     return this.objOficina;
   }
   
   public Oficinas getObjOficina() {
     return getObjOficinas(false);
   }
 
 
   
   public Camiones getObjCamiones(boolean recargar) {
     if (this.objCamion == null || recargar) {
       CamionesDao dao = (CamionesDao)InjectorContainer.getInject().getInstance(CamionesDao.class);
       this.objCamion = dao.find(new Camiones(Integer.parseInt(this.unidad)));
     } 
     return this.objCamion;
   }
   
   public Camiones getObjCamion() {
     return getObjCamiones(false);
   }
 
 
   
   public Operadores getObjOperadores(boolean recargar) {
     if (this.objOperador == null || recargar) {
       OperadoresDao dao = (OperadoresDao)InjectorContainer.getInject().getInstance(OperadoresDao.class);
       this.objOperador = dao.find(new Operadores(this.idOperador));
     } 
     return this.objOperador;
   }
   
   public Operadores getObjOperador() {
     return getObjOperadores(false);
   }
 
 
   
   public Ciudades getObjDestino(boolean recargar) {
     if (this.objDestino == null || recargar) {
       CiudadesDao dao = (CiudadesDao)InjectorContainer.getInject().getInstance(CiudadesDao.class);
       this.objDestino = dao.find(new Ciudades(this.destino));
     } 
     return this.objDestino;
   }
   
   public Ciudades getObjDestino() {
     return getObjDestino(false);
   }
 
 
   
   public Ciudades getObjOrigen(boolean recargar) {
     if (this.objOrigen == null || recargar) {
       CiudadesDao dao = (CiudadesDao)InjectorContainer.getInject().getInstance(CiudadesDao.class);
       this.objOrigen = dao.find(new Ciudades(this.origen));
     } 
     return this.objOrigen;
   }
   
   public Ciudades getObjOrigen() {
     return getObjOrigen(false);
   }
 }


/* Location:              E:\Windows\Desktop\Eliminar\LiquidacionKM.jar!\CustomModel\GuMACustom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */