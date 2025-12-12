package br.upe.data.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Interface base genérica.
 * Define o contrato padrão que todos os repositórios devem seguir.
 * @param <T> O tipo da Entidade (Usuario, Exercicio, etc.)
 */
public interface IGenericRepository<T> {

    T salvar(T entity);

    void editar(T entity);

    void deletar(int id);

    Optional<T> buscarPorId(int id);

    List<T> listarTodos();
}