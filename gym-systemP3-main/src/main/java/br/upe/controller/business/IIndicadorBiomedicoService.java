package br.upe.controller.business;

import br.upe.data.beans.IndicadorBiomedico;

import java.time.LocalDate;
import java.util.List;

public interface IIndicadorBiomedicoService {

    IndicadorBiomedico cadastrarIndicador(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra);

    void importarIndicadoresCsv(int idUsuario, String caminhoArquivoCsv);

    List<IndicadorBiomedico> gerarRelatorioPorData(int idUsuario, LocalDate dataInicio, LocalDate dataFim);

    RelatorioDiferencaIndicadores gerarRelatorioDiferenca(int idUsuario, LocalDate dataInicio, LocalDate dataFim);

    List<IndicadorBiomedico> listarTodosDoUsuario(int idUsuario);

}
