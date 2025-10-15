package br.upe.data.repository;

import br.upe.data.beans.PlanoTreino;

import java.util.List;
import java.util.Optional;

public interface IPlanoTreinoRepository {
    PlanoTreino salvar(PlanoTreino plano);
    List<PlanoTreino> buscarTodosDoUsuario(int idUsuario);
    void editar(PlanoTreino plano);
    void atualizar(PlanoTreino plano);
    void deletar(int idPlano);
    Optional<PlanoTreino> buscarPorNomeEUsuario(int idUsuario, String nomePlano);
    Optional<PlanoTreino> buscarPorId(int idPlano);
    int proximoId();
}