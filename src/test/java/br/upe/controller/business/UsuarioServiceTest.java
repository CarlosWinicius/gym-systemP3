package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioAdmin;
    private Usuario usuarioComum;
    private Usuario outroAdmin;

    @BeforeEach
    void setUp() {
        // Usando a nova entity Usuario (sem construtor com ID diretamente)
        usuarioAdmin = new Usuario("Admin", "admin@email.com", "senha123", TipoUsuario.ADMIN);
        usuarioAdmin.setId(1);

        usuarioComum = new Usuario("Comum", "comum@email.com", "senha456", TipoUsuario.COMUM);
        usuarioComum.setId(2);

        outroAdmin = new Usuario("Outro Admin", "outro@email.com", "senha789", TipoUsuario.ADMIN);
        outroAdmin.setId(3);
    }

    @Test
    @DisplayName("Deve promover usuário comum para admin")
    void testPromoverUsuarioAAdmin_Success() {
        when(usuarioRepository.buscarPorId(2)).thenReturn(Optional.of(usuarioComum));

        usuarioService.promoverUsuarioAAdmin(2);

        assertEquals(TipoUsuario.ADMIN, usuarioComum.getTipo());
        verify(usuarioRepository).editar(usuarioComum);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar promover usuário inexistente")
    void testPromoverUsuarioAAdmin_UsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.promoverUsuarioAAdmin(999);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve fazer nada ao tentar promover usuário que já é admin")
    void testPromoverUsuarioAAdmin_JaAdmin() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuarioAdmin));

        usuarioService.promoverUsuarioAAdmin(1);

        verify(usuarioRepository, never()).editar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve rebaixar usuário admin para comum")
    void testRebaixarUsuarioAComum_Success() {
        when(usuarioRepository.buscarPorId(3)).thenReturn(Optional.of(outroAdmin));

        usuarioService.rebaixarUsuarioAComum(3);

        assertEquals(TipoUsuario.COMUM, outroAdmin.getTipo());
        verify(usuarioRepository).editar(outroAdmin);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar rebaixar admin principal")
    void testRebaixarUsuarioAComum_AdminPrincipal() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.rebaixarUsuarioAComum(1);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar rebaixar usuário inexistente")
    void testRebaixarUsuarioAComum_UsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.rebaixarUsuarioAComum(999);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve fazer nada ao tentar rebaixar usuário que já é comum")
    void testRebaixarUsuarioAComum_JaComum() {
        when(usuarioRepository.buscarPorId(2)).thenReturn(Optional.of(usuarioComum));

        usuarioService.rebaixarUsuarioAComum(2);

        verify(usuarioRepository, never()).editar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais corretas")
    void testAutenticarUsuario_Success() {
        when(usuarioRepository.buscarPorEmail("admin@email.com")).thenReturn(Optional.of(usuarioAdmin));

        Usuario result = usuarioService.autenticarUsuario("admin@email.com", "senha123");

        assertEquals(usuarioAdmin, result);
    }

    @Test
    @DisplayName("Deve retornar null para credenciais incorretas")
    void testAutenticarUsuario_Fail() {
        when(usuarioRepository.buscarPorEmail("admin@email.com")).thenReturn(Optional.of(usuarioAdmin));

        Usuario result = usuarioService.autenticarUsuario("admin@email.com", "senhaErrada");

        assertNull(result);
    }

    @Test
    @DisplayName("Deve lançar exceção para email ou senha vazios")
    void testAutenticarUsuario_EmptyCredentials() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticarUsuario("", "senha");
        });

        assertEquals("Email e senha são obrigatórios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void testCadastrarUsuario_Success() {
        Usuario novoUsuario = new Usuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);
        novoUsuario.setId(3);

        when(usuarioRepository.buscarPorEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(novoUsuario);

        Usuario result = usuarioService.cadastrarUsuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);

        assertNotNull(result);
        assertEquals("Novo", result.getNome());
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para dados vazios no cadastro")
    void testCadastrarUsuario_EmptyData() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("", "email@test.com", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Todos os campos são obrigatórios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email já existente")
    void testCadastrarUsuario_EmailExistente() {
        when(usuarioRepository.buscarPorEmail("admin@email.com")).thenReturn(Optional.of(usuarioAdmin));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("Novo", "admin@email.com", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Já existe um usuário com este email.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void testBuscarUsuarioPorId() {
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuarioAdmin));

        Optional<Usuario> result = usuarioService.buscarUsuarioPorId(1);

        assertTrue(result.isPresent());
        assertEquals(usuarioAdmin, result.get());
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void testBuscarUsuarioPorEmail() {
        when(usuarioRepository.buscarPorEmail("admin@email.com")).thenReturn(Optional.of(usuarioAdmin));

        Optional<Usuario> result = usuarioService.buscarUsuarioPorEmail("admin@email.com");

        assertTrue(result.isPresent());
        assertEquals(usuarioAdmin, result.get());
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void testListarTodosUsuarios() {
        List<Usuario> usuarios = List.of(usuarioAdmin, usuarioComum);
        when(usuarioRepository.listarTodos()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.listarTodosUsuarios();

        assertEquals(2, result.size());
        assertEquals(usuarios, result);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuario_Success() {
        when(usuarioRepository.buscarPorId(2)).thenReturn(Optional.of(usuarioComum));

        usuarioService.atualizarUsuario(2, "Novo Nome", "novo@email.com", "novaSenha", TipoUsuario.ADMIN);

        assertEquals("Novo Nome", usuarioComum.getNome());
        assertEquals("novo@email.com", usuarioComum.getEmail());
        assertEquals("novaSenha", usuarioComum.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuarioComum.getTipo());
        verify(usuarioRepository).editar(usuarioComum);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void testAtualizarUsuario_UsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.atualizarUsuario(999, "Nome", "email@test.com", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para email inválido no cadastro")
    void testCadastrarUsuario_EmailInvalido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("Nome", "emailinvalido", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Email inválido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar usuário com tipo COMUM por padrão")
    void testCadastrarUsuario_TipoComum() {
        Usuario novoUsuario = new Usuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);
        novoUsuario.setId(4);

        when(usuarioRepository.buscarPorEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(novoUsuario);

        Usuario result = usuarioService.cadastrarUsuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);

        assertNotNull(result);
        assertEquals(TipoUsuario.COMUM, result.getTipo());
        verify(usuarioRepository).salvar(any(Usuario.class));
    }
}