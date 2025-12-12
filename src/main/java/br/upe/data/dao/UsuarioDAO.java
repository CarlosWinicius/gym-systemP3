package br.upe.data.dao;

import br.upe.data.entities.EUsuario;
import br.upe.data.infra.ConnectionFactory;
import br.upe.data.interfaces.IUsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IUsuarioRepository {

    @Override
    public EUsuario salvar(EUsuario usuario) {
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
    public void editar(EUsuario usuario) {
        // No JPA, editar e salvar muitas vezes são a mesma operação (merge)
        salvar(usuario);
    }

    @Override
    public void deletar(int id) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            em.getTransaction().begin();
            EUsuario usuario = em.find(EUsuario.class, id);
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
    public Optional<EUsuario> buscarPorId(int id) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            EUsuario usuario = em.find(EUsuario.class, id);
            return Optional.ofNullable(usuario);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<EUsuario> buscarPorEmail(String email) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            String jpql = "SELECT u FROM EUsuario u WHERE u.email = :email";
            TypedQuery<EUsuario> query = em.createQuery(jpql, EUsuario.class);
            query.setParameter("email", email);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<EUsuario> listarTodos() {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            return em.createQuery("FROM EUsuario", EUsuario.class).getResultList();
        } finally {
            em.close();
        }
    }


    public static void atualizarFoto(Integer id, byte[] foto) {
        EntityManager em = ConnectionFactory.getConnection();
        try {
            em.getTransaction().begin();
            EUsuario u = em.find(EUsuario.class, id);
            u.setFotoPerfil(foto);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}