package br.upe.test.dao;

import br.upe.data.entities.Exercicio;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO de Exercicio para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestExercicioDAO implements IExercicioRepository {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    @Override
    public Exercicio salvar(Exercicio entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Exercicio savedEntity = em.merge(entity);
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
    public void editar(Exercicio entity) {
        salvar(entity);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Exercicio entity = em.find(Exercicio.class, id);
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
    public Optional<Exercicio> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            Exercicio entity = em.find(Exercicio.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exercicio> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM Exercicio e";
            TypedQuery<Exercicio> query = em.createQuery(jpql, Exercicio.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exercicio> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM Exercicio e WHERE e.usuario.id = :idUsuario";
            TypedQuery<Exercicio> query = em.createQuery(jpql, Exercicio.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Exercicio> buscarPorNome(String nome) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM Exercicio e WHERE LOWER(e.nome) = LOWER(:nome)";
            TypedQuery<Exercicio> query = em.createQuery(jpql, Exercicio.class);
            query.setParameter("nome", nome);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}

