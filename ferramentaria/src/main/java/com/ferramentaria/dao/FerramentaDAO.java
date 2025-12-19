package com.ferramentaria.dao;


import javax.persistence.EntityManager;

import com.ferramentaria.entity.Ferramenta;
import com.ferramentaria.util.JPAUtil;

public class FerramentaDAO extends GenericDAO<Ferramenta> {

    public FerramentaDAO() {
        super(Ferramenta.class);
    }

    public Ferramenta buscarPorCodigo(String codigo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT f FROM Ferramenta f WHERE f.codigoUnico = :cod";
            return em.createQuery(jpql, Ferramenta.class)
                     .setParameter("cod", codigo)
                     .getSingleResult();
        } catch (Exception e) {
            return null; 
        } finally {
            em.close();
        }
    }
}