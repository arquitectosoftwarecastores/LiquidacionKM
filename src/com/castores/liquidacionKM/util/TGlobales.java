/*    */ package liquidacionkm;
/*    */ 
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class TGlobales
/*    */ {
/*    */   public static String numeros(double cantidad, int decimales, boolean coma) {
/* 11 */     String cad = "";
/* 12 */     for (int i = 0; i < decimales; i++) {
/* 13 */       cad = cad + "0";
/*    */     }
/* 15 */     if (!cad.equals("")) {
/* 16 */       cad = "." + cad;
/*    */     }
/* 18 */     DecimalFormat FMT = new DecimalFormat("###0" + cad);
/* 19 */     if (coma) {
/* 20 */       FMT = new DecimalFormat("#,##0" + cad);
/*    */     }
/* 22 */     return FMT.format(cantidad);
/*    */   }
/*    */   
/*    */   public static String numeros(double cantidad, int decimales) {
/* 26 */     return numeros(cantidad, decimales, true);
/*    */   }
/*    */   
/*    */   public static String numeros(double cantidad, boolean coma) {
/* 30 */     return numeros(cantidad, 2, coma);
/*    */   }
/*    */   
/*    */   public static String numeros(double cantidad) {
/* 34 */     return numeros(cantidad, true);
/*    */   }
/*    */   
/*    */   public static String moneda(double cantidad, int decimales) {
/* 38 */     return "$ " + numeros(cantidad, decimales, true);
/*    */   }
/*    */   
/*    */   public static String moneda(double cantidad) {
/* 42 */     return moneda(cantidad, 2);
/*    */   }
/*    */   
/*    */   public static boolean esEntero(String num) {
/*    */     try {
/* 47 */       Integer.parseInt(num);
/* 48 */       return true;
/* 49 */     } catch (Exception e) {
/* 50 */       System.out.println(e);
/*    */       
/* 52 */       return false;
/*    */     } 
/*    */   }
/*    */   public static boolean esNum(String num) {
/*    */     try {
/* 57 */       Float.parseFloat(num);
/* 58 */       return true;
/* 59 */     } catch (Exception e) {
/* 60 */       System.out.println(e);
/*    */       
/* 62 */       return false;
/*    */     } 
/*    */   }
/*    */   public static String verFecha(Date fecha, String formato) {
/* 66 */     SimpleDateFormat FMT = new SimpleDateFormat(formato);
/* 67 */     return FMT.format(fecha);
/*    */   }
/*    */   
/*    */   public static String verFechaLeyendaCompla(Date fecha) {
/* 71 */     SimpleDateFormat FMT = new SimpleDateFormat("'a' EEEE d 'de' MMMM 'del' yyyy", new Locale("es", "MX"));
/* 72 */     return FMT.format(fecha);
/*    */   }
/*    */   
/*    */   public static String verHora(Date hora) {
/* 76 */     return verFecha(hora, "HH:mm:ss");
/*    */   }
/*    */   
/*    */   public static Date toDate(String fecha, String formato) {
/*    */     try {
/* 81 */       SimpleDateFormat sdf = new SimpleDateFormat(formato, new Locale("es", "MX"));
/* 82 */       return sdf.parse(fecha);
/* 83 */     } catch (Exception e) {
/* 84 */       System.out.println(e);
/*    */       
/* 86 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\Windows\Desktop\Eliminar\LiquidacionKM.jar!\liquidacionkm\TGlobales.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */