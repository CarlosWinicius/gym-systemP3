package br.upe.data.dao;

import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.interfaces.IGenericRepository; // Pode usar a interface genérica direto se não tiver uma específica
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ItemPlanoTreinoDAO extends GenericDAO<ItemPlanoTreino> implements IGenericRepository<ItemPlanoTreino> {

    public ItemPlanoTreinoDAO() {
        super(ItemPlanoTreino.class);
    }

    // Método extra útil: Listar itens de um plano específico
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