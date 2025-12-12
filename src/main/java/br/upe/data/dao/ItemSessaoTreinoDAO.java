package br.upe.data.dao;

import br.upe.data.entities.ItemSessaoTreino;
import br.upe.data.interfaces.IGenericRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ItemSessaoTreinoDAO extends GenericDAO<ItemSessaoTreino> implements IGenericRepository<ItemSessaoTreino> {

    public ItemSessaoTreinoDAO() {
        super(ItemSessaoTreino.class);
    }

    /**
     * Lista todos os itens executados em uma sessão específica.
     * @param idSessao ID da sessão de treino
     * @return Lista de itens realizados
     */
    public List<ItemSessaoTreino> listarPorSessao(int idSessao) {
        EntityManager em = getEntityManager();
        try {
            // JPQL: Seleciona o objeto 'i' onde o atributo 'sessaoTreino.id' é igual ao parâmetro
            String jpql = "SELECT i FROM ItemSessaoTreino i WHERE i.sessaoTreino.id = :idSessao";

            TypedQuery<ItemSessaoTreino> query = em.createQuery(jpql, ItemSessaoTreino.class);
            query.setParameter("idSessao", idSessao);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}