package br.upe.data.dao;

import br.upe.data.entities.Usuario;
import br.upe.data.infra.ConnectionFactory;
import br.upe.data.interfaces.IUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IUsuarioRepository {

    @Override
    public Usuario salvar(Usuario usuario) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            em.getTransaction().begin();
            // Se não tem ID, é novo -> persist. Se tem ID, é atualização -> merge
            if (usuario.getId() == null) {
                em.persist(usuario);
            } else {
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
            return usuario;
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
    public void editar(Usuario usuario) {
        // No JPA, editar e salvar muitas vezes são a mesma operação (merge)
        salvar(usuario);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null) {
                em.remove(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            Usuario usuario = em.find(Usuario.class, id);
            return Optional.ofNullable(usuario);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        EntityManager em = ConnectionFactory.getConnection();
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

    @Override
    public List<Usuario> listarTodos() {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            return em.createQuery("FROM Usuario", Usuario.class).getResultList();
        } finally {
            em.close();
        }
    }


    public static void atualizarFoto(Integer id, byte[] foto) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            em.getTransaction().begin();
            Usuario u = em.find(Usuario.class, id);
            u.setFotoPerfil(foto);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}