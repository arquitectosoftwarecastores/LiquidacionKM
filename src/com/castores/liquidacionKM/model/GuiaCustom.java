package com.castores.liquidacionKM.model;

import com.castores.liquidacionKM.CustomDao.GuMACustomDao;
import castores.core.InjectorContainer;
import castores.model.talones.Guias;
import java.sql.Connection;

public class GuiaCustom
        extends Guias {

    private GuMACustom objGuMA1;

    public void setObjGuMA1(GuMACustom guMACustom) {
        this.objGuMA1 = guMACustom;
    }

    public GuMACustom getObjGuMA1(boolean recargar, Connection conexion) {
        if (this.objGuMA1 == null || recargar) {
            GuMACustomDao guMACustomDao = (GuMACustomDao) InjectorContainer.getInject().getInstance(GuMACustomDao.class);
            guMACustomDao.setTabla(getTabla());
            this.objGuMA1 = (GuMACustom) guMACustomDao.find(new GuMACustom(getNo_guia()), conexion);
        }
        return this.objGuMA1;
    }

    public GuMACustom getObjGuMA1(Connection conexion) {
        return getObjGuMA1(false, conexion);
    }
}
