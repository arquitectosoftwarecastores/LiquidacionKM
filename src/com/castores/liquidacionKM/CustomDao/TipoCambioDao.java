/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.castores.liquidacionKM.CustomDao;

import Injector.AppInjector;
import castores.dao.castores.TipocambioDao;
import castores.dao.talones.TipocambiosoacDao;
import castores.model.castores.Tipocambio;
import castores.model.talones.Tipocambiosoac;
import com.castores.criteriaapi.core.CriteriaBuilder;
import com.google.inject.Injector;
import java.util.List;

public class TipoCambioDao {

    public final double getTipoCambio() {
        Injector injector = AppInjector.getInjector();
        TipocambioDao tipoCambioDao = (TipocambioDao) injector.getInstance(TipocambioDao.class);
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        double tipoCambio = 12.0D;

        criteriaBuilder.eq("estatus", 1);
        criteriaBuilder.orderDesc("fechamod");
        criteriaBuilder.orderDesc("horamod");
        criteriaBuilder.setlimit(1L);
        List<Tipocambio> lastTipoCambio = tipoCambioDao.findBy(criteriaBuilder);
        if (lastTipoCambio.isEmpty()) {
            TipocambiosoacDao dao = (TipocambiosoacDao) injector.getInstance(TipocambiosoacDao.class);
            criteriaBuilder.clear();
            criteriaBuilder.eq("status", 1);
            criteriaBuilder.orderDesc("fechamod");
            criteriaBuilder.orderDesc("horamod");
            criteriaBuilder.setlimit(1L);
            List<Tipocambiosoac> lstTipoCambioSOAC = dao.findBy(criteriaBuilder);
            if (!lstTipoCambioSOAC.isEmpty()) {
                tipoCambio = ((Tipocambiosoac) lstTipoCambioSOAC.get(0)).getValor();
            }
        } else {
            tipoCambio = ((Tipocambio) lastTipoCambio.get(0)).getTipocambio();
        }
        return tipoCambio;
    }
}
