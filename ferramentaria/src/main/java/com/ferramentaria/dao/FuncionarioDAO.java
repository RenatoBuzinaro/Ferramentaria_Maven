package com.ferramentaria.dao;


import javax.persistence.EntityManager;

import com.ferramentaria.entity.Funcionario;
import com.ferramentaria.util.JPAUtil;

public class FuncionarioDAO extends GenericDAO<Funcionario> {

    public FuncionarioDAO() {
        super(Funcionario.class);
    }

    public Funcionario buscarPorMatricula(String matricula) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Funcionario f WHERE f.matricula = :mat", Funcionario.class)
                     .setParameter("mat", matricula)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}