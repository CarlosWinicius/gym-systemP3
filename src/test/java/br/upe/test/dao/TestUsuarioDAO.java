package br.upe.test.dao;

import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IUsuarioRepository;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO de Usuario para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestUsuarioDAO implements IUsuarioRepository {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    @Override
    public Usuario salvar(Usuario entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario savedEntity = em.merge(entity);
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
    public void editar(Usuario entity) {
        salvar(entity);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Usuario entity = em.find(Usuario.class, id);
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
    public Optional<Usuario> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            Usuario entity = em.find(Usuario.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM Usuario u";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.email = :email";
            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}

