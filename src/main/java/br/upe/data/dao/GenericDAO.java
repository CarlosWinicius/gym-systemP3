package br.upe.data.dao;

import br.upe.data.infra.ConnectionFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Classe base para todos os DAOs.
 * T = O tipo da Entidade (Ex: Usuario, Exercicio, etc.)
 */
public abstract class GenericDAO<T> {

    private final Class<T> entityClass;

    // Precisamos receber a classe (Ex: Usuario.class) para o JPA saber o que buscar
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // Método auxiliar para pegar a conexão limpa
    protected EntityManager getEntityManager() {
        return ConnectionFactory.getConnection();
    }

    /**
     * Salva ou Atualiza (Merge) uma entidade no banco.
     */
    public T salvar(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            // O merge serve tanto para INSERT (se não tem ID) quanto UPDATE (se tem ID)
            T savedEntity = em.merge(entity);
            em.getTransaction().commit();
            return savedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Repassa o erro para quem chamou tratar (ex: Controller)
        } finally {
            em.close();
        }
    }

    /**
     * Atualiza os dados (mesma lógica do salvar no JPA)
     */
    public void editar(T entity) {
        salvar(entity);
    }

    /**
     * Remove um registro pelo ID
     */
    public void deletar(int id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
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

    /**
     * Busca um registro pelo ID e retorna um Optional (evita NullPointerException)
     */
    public Optional<T> buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    /**
     * Lista todos os registros da tabela (SELECT * FROM Tabela)
     */
    public List<T> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            // Cria o SQL dinâmico: "SELECT x FROM NomeDaClasse x"
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}