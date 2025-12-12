package br.upe.data.dao;

import br.upe.data.entities.PlanoTreino;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PlanoTreinoDAO extends GenericDAO<PlanoTreino> implements IPlanoTreinoRepository {

    public PlanoTreinoDAO() {
        super(PlanoTreino.class);
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
        editar(plano); // Apenas chama o método do GenericDAO
    }
}