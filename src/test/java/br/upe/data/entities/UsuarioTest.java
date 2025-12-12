package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    @DisplayName("Deve criar usuário com construtor completo")
    void testCriarUsuarioComConstrutor() {
        Usuario usuario = new Usuario("João Silva", "joao@email.com", "senha123", TipoUsuario.COMUM);

        assertNotNull(usuario);
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
        assertEquals(TipoUsuario.COMUM, usuario.getTipo());
        assertNull(usuario.getId());
    }

    @Test
    @DisplayName("Deve criar usuário com construtor vazio")
    void testCriarUsuarioComConstrutorVazio() {
        Usuario usuario = new Usuario();

        assertNotNull(usuario);
        assertNull(usuario.getId());
        assertNull(usuario.getNome());
        assertNull(usuario.getEmail());
        assertNull(usuario.getSenha());
        assertNull(usuario.getTipo());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void testSetEGetId() {
        Usuario usuario = new Usuario();
        usuario.setId(10);

        assertEquals(10, usuario.getId());
    }

    @Test
    @DisplayName("Deve definir e obter nome")
    void testSetEGetNome() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria Santos");

        assertEquals("Maria Santos", usuario.getNome());
    }

    @Test
    @DisplayName("Deve definir e obter email")
    void testSetEGetEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("maria@email.com");

        assertEquals("maria@email.com", usuario.getEmail());
    }

    @Test
    @DisplayName("Deve definir e obter senha")
    void testSetEGetSenha() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senhaSegura");

        assertEquals("senhaSegura", usuario.getSenha());
    }

    @Test
    @DisplayName("Deve definir e obter tipo")
    void testSetEGetTipo() {
        Usuario usuario = new Usuario();
        usuario.setTipo(TipoUsuario.ADMIN);

        assertEquals(TipoUsuario.ADMIN, usuario.getTipo());
    }

    @Test
    @DisplayName("Deve criar usuário admin")
    void testCriarUsuarioAdmin() {
        Usuario admin = new Usuario("Admin", "admin@system.com", "admin123", TipoUsuario.ADMIN);

        assertEquals(TipoUsuario.ADMIN, admin.getTipo());
    }

    @Test
    @DisplayName("Deve gerar toString sem erro")
    void testToString() {
        Usuario usuario = new Usuario("Test", "test@email.com", "senha", TipoUsuario.COMUM);
        usuario.setId(1);

        String resultado = usuario.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Usuario"));
    }
}

