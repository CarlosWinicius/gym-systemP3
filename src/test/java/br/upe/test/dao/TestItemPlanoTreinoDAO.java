package br.upe.test.dao;

import br.upe.data.entities.ItemPlanoTreino;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * DAO de ItemPlanoTreino para testes de integração.
 * Usa o TestConnectionFactory ao invés do ConnectionFactory de produção.
 */
public class TestItemPlanoTreinoDAO {

    protected EntityManager getEntityManager() {
        return TestConnectionFactory.getTestEntityManager();
    }

    public ItemPlanoTreino salvar(ItemPlanoTreino entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            ItemPlanoTreino savedEntity = em.merge(entity);
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

    public void editar(ItemPlanoTreino entity) {
        salvar(entity);
    }

    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            // Usar query direta para evitar problemas de cache
            int deleted = em.createQuery("DELETE FROM ItemPlanoTreino i WHERE i.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
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

    public Optional<ItemPlanoTreino> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            ItemPlanoTreino entity = em.find(ItemPlanoTreino.class, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    public List<ItemPlanoTreino> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM ItemPlanoTreino i";
            TypedQuery<ItemPlanoTreino> query = em.createQuery(jpql, ItemPlanoTreino.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ItemPlanoTreino> listarPorPlano(int idPlano) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM ItemPlanoTreino i WHERE i.planoTreino.id = :idPlano";
            TypedQuery<ItemPlanoTreino> query = em.createQuery(jpql, ItemPlanoTreino.class);
            query.setParameter("idPlano", idPlano);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

