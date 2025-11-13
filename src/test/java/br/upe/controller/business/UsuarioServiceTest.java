package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import br.upe.data.repository.IUsuarioRepository;
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
        usuarioAdmin = new Usuario(1, "Admin", "admin@email.com", "senha123", TipoUsuario.ADMIN);
        usuarioComum = new Usuario(2, "Comum", "comum@email.com", "senha456", TipoUsuario.COMUM);
        outroAdmin = new Usuario(3, "Outro Admin", "outro@email.com", "senha789", TipoUsuario.ADMIN);
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

        assertEquals("Usuário com ID 999 não encontrado para promoção.", exception.getMessage());
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

        assertEquals("O administrador principal não pode ser rebaixado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar rebaixar usuário inexistente")
    void testRebaixarUsuarioAComum_UsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.rebaixarUsuarioAComum(999);
        });

        assertEquals("Usuário com ID 999 não encontrado para rebaixamento.", exception.getMessage());
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

        assertEquals("Email e senha não podem ser vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso")
    void testCadastrarUsuario_Success() {
        when(usuarioRepository.buscarPorEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(new Usuario(3, "Novo", "novo@email.com", "senha", TipoUsuario.COMUM));

        Usuario result = usuarioService.cadastrarUsuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);

        assertNotNull(result);
        assertEquals("Novo", result.getNome());
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para dados vazios no cadastro")
    void testCadastrarUsuario_EmptyData() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("", "email", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Nome, email e senha não podem ser vazios.", exception.getMessage());
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
            usuarioService.atualizarUsuario(999, "Nome", "email", "senha", TipoUsuario.COMUM);
        });

        assertEquals("Usuário com ID 999 não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover usuário com sucesso")
    void testRemoverUsuario_Success() {
        when(usuarioRepository.buscarPorId(2)).thenReturn(Optional.of(usuarioComum));

        usuarioService.removerUsuario(2);

        verify(usuarioRepository).deletar(2);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover admin principal")
    void testRemoverUsuario_AdminPrincipal() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.removerUsuario(1);
        });

        assertEquals("O administrador principal (ID 1) não pode ser removido.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover usuário inexistente")
    void testRemoverUsuario_UsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.removerUsuario(999);
        });

        assertEquals("Usuário com ID 999 não encontrado para remoção.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cadastrar usuário com tipo COMUM por padrão")
    void testCadastrarUsuario_TipoComum() {
        when(usuarioRepository.buscarPorEmail("novo@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(new Usuario(4, "Novo", "novo@email.com", "senha", TipoUsuario.COMUM));

        Usuario result = usuarioService.cadastrarUsuario("Novo", "novo@email.com", "senha", TipoUsuario.COMUM);

        assertNotNull(result);
        assertEquals(TipoUsuario.COMUM, result.getTipo());
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve autenticar usuário com email em maiúsculas")
    void testAutenticarUsuario_EmailMaiusculo() {
        when(usuarioRepository.buscarPorEmail("ADMIN@EMAIL.COM")).thenReturn(Optional.of(usuarioAdmin));

        Usuario result = usuarioService.autenticarUsuario("ADMIN@EMAIL.COM", "senha123");

        assertEquals(usuarioAdmin, result);
    }

    @Test
    @DisplayName("Deve atualizar todos os campos do usuário")
    void testAtualizarUsuario_TodosCampos() {
        when(usuarioRepository.buscarPorId(2)).thenReturn(Optional.of(usuarioComum));

        usuarioService.atualizarUsuario(2, "Atualizado", "atualizado@email.com", "novaSenha", TipoUsuario.ADMIN);

        assertEquals("Atualizado", usuarioComum.getNome());
        assertEquals("atualizado@email.com", usuarioComum.getEmail());
        assertEquals("novaSenha", usuarioComum.getSenha());
        assertEquals(TipoUsuario.ADMIN, usuarioComum.getTipo());
        verify(usuarioRepository).editar(usuarioComum);
    }

    @Test
    @DisplayName("Deve cadastrar usuário ADMIN")
    void testCadastrarUsuario_Admin() {
        when(usuarioRepository.buscarPorEmail("admin2@email.com")).thenReturn(Optional.empty());
        when(usuarioRepository.salvar(any(Usuario.class))).thenReturn(new Usuario(4, "Admin2", "admin2@email.com", "senha", TipoUsuario.ADMIN));

        Usuario result = usuarioService.cadastrarUsuario("Admin2", "admin2@email.com", "senha", TipoUsuario.ADMIN);

        assertNotNull(result);
        assertEquals(TipoUsuario.ADMIN, result.getTipo());
        verify(usuarioRepository).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void testListarTodosUsuarios_Multiplos() {
        List<Usuario> usuarios = List.of(usuarioAdmin, usuarioComum, outroAdmin);
        when(usuarioRepository.listarTodos()).thenReturn(usuarios);

        List<Usuario> result = usuarioService.listarTodosUsuarios();

        assertEquals(3, result.size());
        assertEquals(usuarios, result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar com email nulo")
    void testCadastrarUsuario_EmailNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("Nome", null, "senha", TipoUsuario.COMUM);
        });

        assertEquals("Nome, email e senha não podem ser vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar com senha vazia")
    void testCadastrarUsuario_SenhaVazia() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarUsuario("Nome", "email@test.com", "", TipoUsuario.COMUM);
        });

        assertEquals("Nome, email e senha não podem ser vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com email vazio")
    void testAutenticarUsuario_EmailVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticarUsuario("", "senha");
        });

        assertEquals("Email e senha não podem ser vazios.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao autenticar com senha nula")
    void testAutenticarUsuario_SenhaNula() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.autenticarUsuario("email@test.com", null);
        });

        assertEquals("Email e senha não podem ser vazios.", exception.getMessage());
    }
}

