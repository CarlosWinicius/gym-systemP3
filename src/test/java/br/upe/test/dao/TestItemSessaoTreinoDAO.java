package br.upe.test.dao;

import br.upe.data.entities.ItemSessaoTreino;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO de ItemSessaoTreino para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestItemSessaoTreinoDAO {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    public ItemSessaoTreino salvar(ItemSessaoTreino entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ItemSessaoTreino savedEntity = em.merge(entity);
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

    public void editar(ItemSessaoTreino entity) {
        salvar(entity);
    }

    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ItemSessaoTreino entity = em.find(ItemSessaoTreino.class, id);
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

    public Optional<ItemSessaoTreino> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            ItemSessaoTreino entity = em.find(ItemSessaoTreino.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    public List<ItemSessaoTreino> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM ItemSessaoTreino i";
            TypedQuery<ItemSessaoTreino> query = em.createQuery(jpql, ItemSessaoTreino.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ItemSessaoTreino> listarPorSessao(int idSessao) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM ItemSessaoTreino i WHERE i.sessaoTreino.id = :idSessao";
            TypedQuery<ItemSessaoTreino> query = em.createQuery(jpql, ItemSessaoTreino.class);
            query.setParameter("idSessao", idSessao);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

