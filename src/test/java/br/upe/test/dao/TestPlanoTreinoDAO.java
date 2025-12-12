package br.upe.test.dao;

import br.upe.data.entities.PlanoTreino;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO de PlanoTreino para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestPlanoTreinoDAO implements IPlanoTreinoRepository {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    @Override
    public PlanoTreino salvar(PlanoTreino entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            PlanoTreino savedEntity = em.merge(entity);
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
    public void editar(PlanoTreino entity) {
        salvar(entity);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            PlanoTreino entity = em.find(PlanoTreino.class, id);
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
    public Optional<PlanoTreino> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            PlanoTreino entity = em.find(PlanoTreino.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PlanoTreino> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM PlanoTreino p";
            TypedQuery<PlanoTreino> query = em.createQuery(jpql, PlanoTreino.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<PlanoTreino> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM PlanoTreino p WHERE p.usuario.id = :idUsuario";
            TypedQuery<PlanoTreino> query = em.createQuery(jpql, PlanoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<PlanoTreino> buscarPorNomeEUsuario(int idUsuario, String nomePlano) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT p FROM PlanoTreino p WHERE p.usuario.id = :idUsuario AND LOWER(p.nome) = LOWER(:nome)";
            TypedQuery<PlanoTreino> query = em.createQuery(jpql, PlanoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("nome", nomePlano);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(PlanoTreino plano) {
        editar(plano);
    }
}

