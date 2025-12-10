package br.upe.data.dao;

import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class IndicadorBiomedicoDAO extends GenericDAO<IndicadorBiomedico> implements IIndicadorBiomedicoRepository {

    public IndicadorBiomedicoDAO() {
        super(IndicadorBiomedico.class);
    }

    @Override
    public List<IndicadorBiomedico> listarPorUsuario(int idUsuario) {
        EntityManager em = getEntityManager();
        try {
            // Ordena por data decrescente (mais recente primeiro)
            String jpql = "SELECT i FROM IndicadorBiomedico i WHERE i.usuario.id = :idUsuario ORDER BY i.data DESC";
            TypedQuery<IndicadorBiomedico> query = em.createQuery(jpql, IndicadorBiomedico.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<IndicadorBiomedico> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT i FROM IndicadorBiomedico i WHERE i.usuario.id = :idUsuario " +
                    "AND i.data BETWEEN :inicio AND :fim ORDER BY i.data ASC";
            TypedQuery<IndicadorBiomedico> query = em.createQuery(jpql, IndicadorBiomedico.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("inicio", dataInicio);
            query.setParameter("fim", dataFim);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}