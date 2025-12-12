package br.upe.data.dao;

import br.upe.data.entities.Exercicio;
import br.upe.data.interfaces.IExercicioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

// 1. Extende GenericDAO: Ganha salvar, editar, deletar e buscarPorId de graça
// 2. Implementa IExercicioRepository: Garante que segue o contrato
public class ExercicioDAO extends GenericDAO<Exercicio> implements IExercicioRepository {

    public ExercicioDAO() {
        super(Exercicio.class);
    }

    // --- Métodos Específicos (que o GenericDAO não sabe fazer) ---

    @Override
    public List<Exercicio> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            // JPQL: Navega pelo objeto usuario (u.usuario.id)
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