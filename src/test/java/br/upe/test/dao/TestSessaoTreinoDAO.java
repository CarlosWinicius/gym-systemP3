package br.upe.test.dao;

import br.upe.data.entities.SessaoTreino;
import br.upe.data.interfaces.ISessaoTreinoRepository;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO de SessaoTreino para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestSessaoTreinoDAO implements ISessaoTreinoRepository {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    @Override
    public SessaoTreino salvar(SessaoTreino entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            SessaoTreino savedEntity = em.merge(entity);
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
    public void editar(SessaoTreino entity) {
        salvar(entity);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            SessaoTreino entity = em.find(SessaoTreino.class, id);
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
    public Optional<SessaoTreino> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            SessaoTreino entity = em.find(SessaoTreino.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessaoTreino> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT s FROM SessaoTreino s";
            TypedQuery<SessaoTreino> query = em.createQuery(jpql, SessaoTreino.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessaoTreino> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario ORDER BY s.dataSessao DESC";
            TypedQuery<SessaoTreino> query = em.createQuery(jpql, SessaoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessaoTreino> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario " +
                    "AND s.dataSessao BETWEEN :inicio AND :fim ORDER BY s.dataSessao DESC";
            TypedQuery<SessaoTreino> query = em.createQuery(jpql, SessaoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("inicio", dataInicio);
            query.setParameter("fim", dataFim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

