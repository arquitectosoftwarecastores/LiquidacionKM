package Injector;

import castores.core.InjectorContainer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class AppInjector
{
  private static Injector inject;
  
  public static Injector getInjector() {
    if (inject == null) {
      
      synchronized (AppInjector.class) {
        if (inject == null) {
          inject = Guice.createInjector(new Module[] { new PersistenciaModule() });
          InjectorContainer injectorContainer = InjectorContainer.getInstance(inject);
        } 
      } 
      return inject;
    } 
    return inject;
  }
}