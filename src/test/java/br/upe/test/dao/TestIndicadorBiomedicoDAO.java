package br.upe.test.dao;

import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO de IndicadorBiomedico para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestIndicadorBiomedicoDAO implements IIndicadorBiomedicoRepository {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    @Override
    public IndicadorBiomedico salvar(IndicadorBiomedico entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            IndicadorBiomedico savedEntity = em.merge(entity);
            em.getTransaction().commit();
            return savedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void editar(IndicadorBiomedico entity) {
        salvar(entity);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            IndicadorBiomedico entity = em.find(IndicadorBiomedico.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<IndicadorBiomedico> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            IndicadorBiomedico entity = em.find(IndicadorBiomedico.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<IndicadorBiomedico> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM IndicadorBiomedico i";
            TypedQuery<IndicadorBiomedico> query = em.createQuery(jpql, IndicadorBiomedico.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<IndicadorBiomedico> listarPorUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM IndicadorBiomedico i WHERE i.usuario.id = :idUsuario ORDER BY i.dataRegistro DESC";
            TypedQuery<IndicadorBiomedico> query = em.createQuery(jpql, IndicadorBiomedico.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<IndicadorBiomedico> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM IndicadorBiomedico i WHERE i.usuario.id = :idUsuario " +
                    "AND i.dataRegistro BETWEEN :inicio AND :fim ORDER BY i.dataRegistro ASC";
            TypedQuery<IndicadorBiomedico> query = em.createQuery(jpql, IndicadorBiomedico.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("inicio", dataInicio);
            query.setParameter("fim", dataFim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

