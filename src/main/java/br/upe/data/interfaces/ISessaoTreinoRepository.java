package br.upe.data.interfaces;

import br.upe.data.entities.SessaoTreino;
import java.time.LocalDate;
import java.util.List;

public interface ISessaoTreinoRepository extends IGenericRepository<SessaoTreino> {

    List<SessaoTreino> buscarTodosDoUsuario(int idUsuario);
    List<SessaoTreino> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim);
}