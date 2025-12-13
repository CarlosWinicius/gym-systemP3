// java
package br.upe;

import br.upe.data.TipoUsuario;
import br.upe.data.dao.UsuarioDAO;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IUsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioRepositoryImplTest {

    private IUsuarioRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UsuarioDAO();
        // Limpar todos os usuários antes de cada teste (uma única iteração)
        List<Usuario> lista = repository.listarTodos();
        for (Usuario u : lista) {
            try {
                repository.deletar(u.getId());
            } catch (Exception ignored) {
                // ignorar erros de remoção individuais durante a limpeza
            }
        }
    }

    @AfterEach
    void tearDown() {
        // Limpeza adicional após cada teste (uma única iteração)
        List<Usuario> lista = repository.listarTodos();
        for (Usuario u : lista) {
            try {
                repository.deletar(u.getId());
            } catch (Exception ignored) {
                // ignorar erros de remoção individuais durante a limpeza
            }
        }
    }

    // Helper: gera email único a partir de um base
    private String uniqueEmail(String base) {
        String[] parts = base.split("@", 2);
        String local = parts[0];
        String domain = parts.length > 1 ? parts[1] : "example.com";
        return local + "+" + System.nanoTime() + "@" + domain;
    }

    // Helper: cria e salva usuário com email único
    private Usuario novoUsuarioSalvo(String nome, String emailBase, String senha, TipoUsuario tipo) {
        String email = uniqueEmail(emailBase);
        Usuario usuario = new Usuario(nome, email, senha, tipo);
        return repository.salvar(usuario);
    }

    @Test
    void testSalvarEBuscarPorId() {
        Usuario salvo = novoUsuarioSalvo("Joao", "joao@example.com", "senha123", TipoUsuario.COMUM);
        assertNotEquals(0, salvo.getId());
        Optional<Usuario> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Joao", buscado.get().getNome());
    }

    @Test
    void testBuscarPorEmail() {
        String baseEmail = "maria@example.com";
        Usuario usuario = novoUsuarioSalvo("Maria", baseEmail, "senha456", TipoUsuario.COMUM);
        Optional<Usuario> buscado = repository.buscarPorEmail(usuario.getEmail());
        assertTrue(buscado.isPresent());
        assertEquals("Maria", buscado.get().getNome());
    }

    @Test
    void testListarTodos() {
        novoUsuarioSalvo("Joao", "joao@example.com", "senha123", TipoUsuario.COMUM);
        novoUsuarioSalvo("Maria", "maria@example.com", "senha456", TipoUsuario.ADMIN);
        List<Usuario> usuarios = repository.listarTodos();
        assertTrue(usuarios.size() >= 2);
    }

    @Test
    void testEditar() {
        Usuario salvo = novoUsuarioSalvo("Joao", "joao@example.com", "senha123", TipoUsuario.COMUM);
        int idSalvo = salvo.getId();
        salvo.setNome("Joao Silva");
        repository.editar(salvo);
        Optional<Usuario> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Joao Silva", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        Usuario salvo = novoUsuarioSalvo("Joao", "joao@example.com", "senha123", TipoUsuario.COMUM);
        int idSalvo = salvo.getId();
        repository.deletar(idSalvo);
        Optional<Usuario> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Usuario> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testBuscarPorEmailInexistente() {
        Optional<Usuario> buscado = repository.buscarPorEmail("inexistente+" + System.nanoTime() + "@example.com");
        assertFalse(buscado.isPresent());
    }

    @Test
    void testEditarUsuarioInexistente() {
        Usuario usuario = new Usuario();
        usuario.setId(999);
        usuario.setNome("Nome");
        usuario.setEmail(uniqueEmail("email@test.com"));
        usuario.setSenha("senha");
        usuario.setTipo(TipoUsuario.COMUM);

        repository.editar(usuario);
        Optional<Usuario> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarUsuarioInexistente() {
        repository.deletar(999);
        List<Usuario> usuarios = repository.listarTodos();
        assertNotNull(usuarios);
    }

    @Test
    void testSalvarUsuarioComIdExistente() {
        Usuario usuario = novoUsuarioSalvo("Joao", "joao@example.com", "senha123", TipoUsuario.COMUM);
        int idOriginal = usuario.getId();

        usuario.setNome("Joao Atualizado");
        Usuario atualizado = repository.salvar(usuario);

        assertEquals(idOriginal, atualizado.getId());
        assertEquals("Joao Atualizado", atualizado.getNome());
    }

    @Test
    void testGerarProximoId() {
        Usuario salvo = novoUsuarioSalvo("Temp", "temp@example.com", "s", TipoUsuario.COMUM);
        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);
    }

    @Test
    void testPersistenciaEmArquivo() {
        String baseEmail = "test@example.com";
        Usuario usuario = novoUsuarioSalvo("Test", baseEmail, "senha", TipoUsuario.COMUM);

        IUsuarioRepository novoRepository = new UsuarioDAO();
        Optional<Usuario> recuperado = novoRepository.buscarPorEmail(usuario.getEmail());

        assertTrue(recuperado.isPresent());
        assertEquals("Test", recuperado.get().getNome());
    }
}
