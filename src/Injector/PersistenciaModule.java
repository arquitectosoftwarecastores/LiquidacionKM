package Injector;

import castores.core.Persistencia;
import castores.core.PersistenciaLocal;
import com.castores.datautilsapi.log.LoggerUtils;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;

public class PersistenciaModule
        implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Persistencia.class).to(PersistenciaLocal.class);

        Properties configuracion = new Properties();
        try {
            configuracion.load(new FileInputStream("persistencia.properties"));
        } catch (IOException excepcionGeneral) {
            try {
                File ejecutable = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                String separacion = System.getProperty("file.separator");
                String ruta = ejecutable.getAbsolutePath();
                ruta = ruta.substring(0, ruta.lastIndexOf(separacion)) + separacion;
                configuracion.load(new FileInputStream(ruta + "persistencia.properties"));
            } catch (IOException excepcion) {
                LoggerUtils.printLog(this.getClass(), Level.SEVERE, excepcion,excepcion.getLocalizedMessage(), Thread.currentThread().getStackTrace());
            } catch (URISyntaxException excepcion) {
                LoggerUtils.printLog(this.getClass(), Level.SEVERE, excepcion,excepcion.getLocalizedMessage(), Thread.currentThread().getStackTrace());
            }
        }
        binder.bindConstant().annotatedWith((Annotation) Names.named("Server13")).to(configuracion.getProperty("Server13") + "&usuarioWin&windows");
        binder.bindConstant().annotatedWith((Annotation) Names.named("Server23")).to(configuracion.getProperty("Server23") + "&usuarioWin&windows");
    }
}
