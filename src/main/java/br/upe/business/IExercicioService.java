package br.upe.business;

import br.upe.data.beans.Exercicio;

import java.util.List;
import java.util.Optional;

public interface IExercicioService {
    Exercicio cadastrarExercicio(int idUsuario, String nome, String descricao, String caminhoGif);
    List<Exercicio> listarExerciciosDoUsuario(int idUsuario);
    Optional<Exercicio> buscarExercicioDoUsuarioPorNome(int idUsuario, String nomeExercicio);
    boolean deletarExercicioPorNome(int idUsuario, String nomeExercicio);
    void atualizarExercicio(int idUsuario, String nomeAtualExercicio, String novoNome, String novaDescricao, String novoCaminhoGif);
}
