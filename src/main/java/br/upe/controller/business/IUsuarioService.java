package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.EUsuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    EUsuario autenticarUsuario(String email, String senha);
    EUsuario cadastrarUsuario(String nome, String email, String senha, TipoUsuario tipo);

    Optional<EUsuario> buscarUsuarioPorId(int id);
    Optional<EUsuario> buscarUsuarioPorEmail(String email);
    List<EUsuario> listarTodosUsuarios();
    void atualizarUsuario(int id, String novoNome, String novoEmail, String novaSenha, TipoUsuario novoTipo);
    void removerUsuario(int id);

    void promoverUsuarioAAdmin(int idUsuario);
    void rebaixarUsuarioAComum(int idUsuario);
    void atualizarFoto(Integer id, byte[] foto);
}