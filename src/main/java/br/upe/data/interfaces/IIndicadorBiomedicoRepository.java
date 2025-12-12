package br.upe.data.interfaces;

import br.upe.data.entities.IndicadorBiomedico;
import java.time.LocalDate;
import java.util.List;

public interface IIndicadorBiomedicoRepository extends IGenericRepository<IndicadorBiomedico> {

    List<IndicadorBiomedico> listarPorUsuario(int idUsuario);
    List<IndicadorBiomedico> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim);
}