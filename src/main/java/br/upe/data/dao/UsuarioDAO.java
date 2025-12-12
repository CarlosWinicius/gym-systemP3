package br.upe.data.dao;

import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class UsuarioDAO extends GenericDAO<Usuario> implements IUsuarioRepository {

    public UsuarioDAO() {
        super(Usuario.class);
    }


    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = getEntityManager(); // Método herdado do GenericDAO
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