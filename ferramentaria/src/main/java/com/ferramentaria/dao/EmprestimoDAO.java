package com.ferramentaria.dao;


import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import com.ferramentaria.entity.Emprestimo;
import com.ferramentaria.util.JPAUtil;

public class EmprestimoDAO extends GenericDAO<Emprestimo> {
    
    public EmprestimoDAO() {
        super(Emprestimo.class);
    }

    public Emprestimo buscarAbertoPorFerramenta(Long idFerramenta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e WHERE e.ferramenta.id = :id AND e.dataDevolucao IS NULL";
            return em.createQuery(jpql, Emprestimo.class)
                     .setParameter("id", idFerramenta)
                     .getSingleResult();
        } catch (Exception e) {
            return null; 
        } finally {
            em.close();
        }
    }

    public List<Emprestimo> listarHistoricoPorFerramenta(Long idFerramenta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e WHERE e.ferramenta.id = :id ORDER BY e.dataEmprestimo DESC";
            return em.createQuery(jpql, Emprestimo.class)
                     .setParameter("id", idFerramenta)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprestimo> listarHistoricoPorFuncionario(Long idFuncionario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e WHERE e.funcionario.id = :id ORDER BY e.dataEmprestimo DESC";
            return em.createQuery(jpql, Emprestimo.class)
                     .setParameter("id", idFuncionario)
                     .getResultList();
        } finally {
            em.close();
        }
    }


    public List<Emprestimo> listarUltimos(int limite) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e ORDER BY e.dataEmprestimo DESC";
            return em.createQuery(jpql, Emprestimo.class)
                     .setMaxResults(limite) 
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprestimo> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM Emprestimo e WHERE e.dataEmprestimo BETWEEN :inicio AND :fim ORDER BY e.dataEmprestimo DESC";
            return em.createQuery(jpql, Emprestimo.class)
                     .setParameter("inicio", inicio)
                     .setParameter("fim", fim)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}