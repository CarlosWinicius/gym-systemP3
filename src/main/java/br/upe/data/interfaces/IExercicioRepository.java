package br.upe.data.interfaces;

import br.upe.data.entities.Exercicio;
import java.util.List;
import java.util.Optional;

public interface IExercicioRepository extends IGenericRepository<Exercicio> {

    List<Exercicio> buscarTodosDoUsuario(int idUsuario);
    Optional<Exercicio> buscarPorNome(String nome);
}