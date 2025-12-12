package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    Usuario autenticarUsuario(String email, String senha);
    Usuario cadastrarUsuario(String nome, String email, String senha, TipoUsuario tipo);

    Optional<Usuario> buscarUsuarioPorId(int id);
    Optional<Usuario> buscarUsuarioPorEmail(String email);
    List<Usuario> listarTodosUsuarios();
    void atualizarUsuario(int id, String novoNome, String novoEmail, String novaSenha, TipoUsuario novoTipo);
    void removerUsuario(int id);

    void promoverUsuarioAAdmin(int idUsuario);
    void rebaixarUsuarioAComum(int idUsuario);
}