package br.upe.data.repository;

import br.upe.data.beans.SessaoTreino;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ISessaoTreinoRepository {
    SessaoTreino salvar(SessaoTreino sessao);
    Optional<SessaoTreino> buscarPorId(int idSessao);
    List<SessaoTreino> buscarTodosDoUsuario(int idUsuario);
    List<SessaoTreino> buscarPorPeriodo(int idUsuario, LocalDate dataInicio, LocalDate dataFim);
    void editar(SessaoTreino sessao);
    void deletar(int idSessao);
    int proximoId();
}