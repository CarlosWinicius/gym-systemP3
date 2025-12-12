package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.EUsuario;
import br.upe.data.interfaces.IUsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());

    private static final String EMAIL_SUPER_ADMIN = "adm@email.com";
    private static final String SENHA_SUPER_ADMIN = "adm";
    private static final String NOME_SUPER_ADMIN = "Administrador";

    private static final String MSG_EMAIL_SENHA_OBRIGATORIOS = "Email e senha são obrigatórios.";
    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado.";
    private static final String MSG_SUPER_ADMIN_RESTRICAO = "O Super Administrador não pode sofrer esta alteração.";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioService() {
        this.usuarioRepository = new UsuarioDAO();
    }

    public void verificarECriarAdminPadrao() {
        Optional<EUsuario> admin = usuarioRepository.buscarPorEmail(EMAIL_SUPER_ADMIN);

        if (admin.isEmpty()) {
            EUsuario superAdmin = new EUsuario();
            superAdmin.setNome(NOME_SUPER_ADMIN);
            superAdmin.setEmail(EMAIL_SUPER_ADMIN);
            superAdmin.setSenha(SENHA_SUPER_ADMIN);
            superAdmin.setTipo(TipoUsuario.ADMIN);

            usuarioRepository.salvar(superAdmin);
            LOGGER.log(Level.INFO, "Super Admin criado com sucesso: {0}", EMAIL_SUPER_ADMIN);
        }
    }

    @Override
    public EUsuario autenticarUsuario(String email, String senha) {
        if (isStringInvalid(email) || isStringInvalid(senha)) {
            throw new IllegalArgumentException(MSG_EMAIL_SENHA_OBRIGATORIOS);
        }

        Optional<EUsuario> usuarioOpt = usuarioRepository.buscarPorEmail(email.trim());

        if (usuarioOpt.isPresent()) {
            EUsuario usuario = usuarioOpt.get();
            if (usuario.getSenha().equals(senha)) {
                return usuario;
            }
        }

        throw new IllegalArgumentException("Email ou senha inválidos.");
    }

    @Override
    public EUsuario cadastrarUsuario(String nome, String email, String senha, TipoUsuario tipo) {
        if (isStringInvalid(nome) || isStringInvalid(email) || isStringInvalid(senha)) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios.");
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }

        if (usuarioRepository.buscarPorEmail(email.trim()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com este email.");
        }

        EUsuario novoUsuario = new EUsuario(nome.trim(), email.trim(), senha.trim(), tipo);
        return usuarioRepository.salvar(novoUsuario);
    }

    @Override
    public void removerUsuario(int id) {
        EUsuario usuario = buscarUsuarioOuLancarExcecao(id);

        if (EMAIL_SUPER_ADMIN.equalsIgnoreCase(usuario.getEmail())) {
            throw new IllegalArgumentException("O Super Administrador não pode ser excluído.");
        }

        usuarioRepository.deletar(id);
    }

    @Override
    public void atualizarUsuario(int id, String novoNome, String novoEmail, String novaSenha, TipoUsuario novoTipo) {
        EUsuario usuario = buscarUsuarioOuLancarExcecao(id);

        validarRestricoesSuperAdmin(usuario, novoEmail, novoTipo);

        if (!isStringInvalid(novoNome)) usuario.setNome(novoNome.trim());
        if (!isStringInvalid(novaSenha)) usuario.setSenha(novaSenha.trim());

        processarAtualizacaoEmail(usuario, novoEmail);

        if (novoTipo != null) usuario.setTipo(novoTipo);

        usuarioRepository.editar(usuario);
    }

    @Override
    public void rebaixarUsuarioAComum(int idUsuario) {
        EUsuario usuario = buscarUsuarioOuLancarExcecao(idUsuario);

        if (EMAIL_SUPER_ADMIN.equalsIgnoreCase(usuario.getEmail())) {
            throw new IllegalArgumentException(MSG_SUPER_ADMIN_RESTRICAO);
        }

        if (usuario.getTipo() == TipoUsuario.COMUM) return;

        usuario.setTipo(TipoUsuario.COMUM);
        usuarioRepository.editar(usuario);
    }

    @Override
    public void promoverUsuarioAAdmin(int idUsuario) {
        EUsuario usuario = buscarUsuarioOuLancarExcecao(idUsuario);
        if (usuario.getTipo() == TipoUsuario.ADMIN) return;

        usuario.setTipo(TipoUsuario.ADMIN);
        usuarioRepository.editar(usuario);
    }

    private void validarRestricoesSuperAdmin(EUsuario usuario, String novoEmail, TipoUsuario novoTipo) {
        boolean isSuperAdmin = EMAIL_SUPER_ADMIN.equalsIgnoreCase(usuario.getEmail());

        if (isSuperAdmin) {
            if (novoTipo != null && novoTipo != TipoUsuario.ADMIN) {
                throw new IllegalArgumentException("O Super Administrador deve permanecer como ADMIN.");
            }
            if (novoEmail != null && !novoEmail.equalsIgnoreCase(EMAIL_SUPER_ADMIN)) {
                throw new IllegalArgumentException("O e-mail do Super Administrador não pode ser alterado.");
            }
        }
    }

    private void processarAtualizacaoEmail(EUsuario usuario, String novoEmail) {
        if (!isStringInvalid(novoEmail) && !novoEmail.equalsIgnoreCase(usuario.getEmail())) {
            Optional<EUsuario> emailExistente = usuarioRepository.buscarPorEmail(novoEmail.trim());

            if (emailExistente.isPresent() && !emailExistente.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Este e-mail já está em uso.");
            }
            usuario.setEmail(novoEmail.trim());
        }
    }

    private EUsuario buscarUsuarioOuLancarExcecao(int id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(MSG_USUARIO_NAO_ENCONTRADO));
    }

    private boolean isStringInvalid(String str) {
        return str == null || str.isBlank();
    }

    @Override
    public Optional<EUsuario> buscarUsuarioPorId(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    @Override
    public Optional<EUsuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    @Override
    public List<EUsuario> listarTodosUsuarios() {
        return usuarioRepository.listarTodos();
    }

    @Override
    public void atualizarFoto(Integer id, byte[] foto) {
        EUsuario usuario = buscarUsuarioOuLancarExcecao(id);
        usuario.setFotoPerfil(foto);
        usuarioRepository.editar(usuario);
    }

}