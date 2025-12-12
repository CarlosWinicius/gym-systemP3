package br.upe.data.dao;

import br.upe.data.entities.SessaoTreino;
import br.upe.data.interfaces.ISessaoTreinoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class SessaoTreinoDAO extends GenericDAO<SessaoTreino> implements ISessaoTreinoRepository {

    public SessaoTreinoDAO() {
        // Informa ao GenericDAO que esta classe cuida de 'SessaoTreino'
        super(SessaoTreino.class);
    }

    @Override
    public List<SessaoTreino> buscarTodosDoUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            // Traz as sessões ordenadas da mais recente para a mais antiga
            String jpql = "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario ORDER BY s.dataSessao DESC";
            TypedQuery<SessaoTreino> query = em.createQuery(jpql, SessaoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<SessaoTreino> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        EntityManager em = getEntityManager();
        try {
            // Filtra por usuário E pelo intervalo de datas (BETWEEN)
            String jpql = "SELECT s FROM SessaoTreino s WHERE s.usuario.id = :idUsuario " +
                    "AND s.dataSessao BETWEEN :inicio AND :fim ORDER BY s.dataSessao DESC";
            TypedQuery<SessaoTreino> query = em.createQuery(jpql, SessaoTreino.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("inicio", dataInicio);
            query.setParameter("fim", dataFim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}