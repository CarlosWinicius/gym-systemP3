package br.upe.data.interfaces;

import br.upe.data.entities.EUsuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository {
    EUsuario salvar(EUsuario usuario);
    Optional<EUsuario> buscarPorId(int id);
    Optional<EUsuario> buscarPorEmail(String email);
    List<EUsuario> listarTodos();
    void editar(EUsuario usuario);
    void deletar(int id);
}