package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import br.upe.data.repository.IUsuarioRepository;
import br.upe.data.repository.impl.UsuarioRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class UsuarioService implements IUsuarioService {
    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    @Override
    public Usuario autenticarUsuario(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e senha não podem ser vazios.");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(email.trim());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public Usuario cadastrarUsuario(String nome, String email, String senha, TipoUsuario tipo) {
        if (nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome, email e senha não podem ser vazios.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        Optional<Usuario> existente = usuarioRepository.buscarPorEmail(email.trim());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este email.");
        }

        Usuario novoUsuario = new Usuario(nome.trim(), email.trim(), senha.trim(), tipo);
        return usuarioRepository.salvar(novoUsuario);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    @Override
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public void atualizarUsuario(int id, String novoNome, String novoEmail, String novaSenha, TipoUsuario novoTipo) {
        if (id == 1 && novoTipo != null && novoTipo == TipoUsuario.COMUM) {
            throw new IllegalArgumentException("O administrador principal não pode ser rebaixado para usuário comum.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(id);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado.");
        }
        Usuario usuario = usuarioOpt.get();

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            usuario.setNome(novoNome.trim());
        }
        if (novoEmail != null && !novoEmail.trim().isEmpty()) {
            if (!novoEmail.trim().equalsIgnoreCase(usuario.getEmail())) {
                Optional<Usuario> emailExistente = usuarioRepository.buscarPorEmail(novoEmail.trim());
                if (emailExistente.isPresent() && emailExistente.get().getId() != usuario.getId()) {
                    throw new IllegalArgumentException("Email '" + novoEmail + "' já está em uso por outro usuário.");
                }
            }
            usuario.setEmail(novoEmail.trim());
        }
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            usuario.setSenha(novaSenha.trim());
        }
        if (novoTipo != null) {
            usuario.setTipo(novoTipo);
        }
        usuarioRepository.editar(usuario);
    }

    @Override
    public void removerUsuario(int id) {
        if (id == 1) {
            throw new IllegalArgumentException("O administrador principal (ID 1) não pode ser removido.");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(id);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuário com ID " + id + " não encontrado para remoção.");
        }
        usuarioRepository.deletar(id);
    }

    @Override
    public void promoverUsuarioAAdmin(int idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário com ID " + idUsuario + " não encontrado para promoção.");
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getTipo() == TipoUsuario.ADMIN) {
            System.out.println("Usuário já é ADMIN.");
            return;
        }
        usuario.setTipo(TipoUsuario.ADMIN);
        usuarioRepository.editar(usuario);
        System.out.println("Usuário " + usuario.getNome() + " promovido a ADMIN.");
    }

    @Override
    public void rebaixarUsuarioAComum(int idUsuario) {
        if (idUsuario == 1) {
            throw new IllegalArgumentException("O administrador principal não pode ser rebaixado.");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário com ID " + idUsuario + " não encontrado para rebaixamento.");
        }
        Usuario usuario = usuarioOpt.get();
        if (usuario.getTipo() == TipoUsuario.COMUM) {
            System.out.println("Usuário já é COMUM.");
            return;
        }
        usuario.setTipo(TipoUsuario.COMUM);
        usuarioRepository.editar(usuario);
        System.out.println("Usuário " + usuario.getNome() + " rebaixado a COMUM.");
    }
}