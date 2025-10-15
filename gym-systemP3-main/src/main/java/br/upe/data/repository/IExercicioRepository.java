package br.upe.data.repository;

import br.upe.data.beans.Exercicio;

import java.util.List;
import java.util.Optional;

public interface IExercicioRepository {
    Exercicio salvar(Exercicio exercicio);
    List<Exercicio> buscarTodosDoUsuario(int idUsuario);
    void editar(Exercicio exercicio);
    void deletar(int idExercicio);
    Optional<Exercicio> buscarPorNome(String nome);
    int proximoId();
    Optional<Exercicio> buscarPorId(int idExercicio);
}