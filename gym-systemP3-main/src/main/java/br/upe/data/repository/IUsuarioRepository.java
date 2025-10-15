package br.upe.data.repository;

import br.upe.data.beans.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(int id);
    Optional<Usuario> buscarPorEmail(String email);
    List<Usuario> listarTodos();
    void editar(Usuario usuario);
    void deletar(int id);
    int gerarProximoId();
}