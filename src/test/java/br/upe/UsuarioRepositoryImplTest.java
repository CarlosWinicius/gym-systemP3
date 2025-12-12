package br.upe;

import br.upe.data.entities.Usuario;
import br.upe.data.TipoUsuario;
import br.upe.data.interfaces.IUsuarioRepository;
import br.upe.data.dao.UsuarioDAO;
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
        // Usando DAO baseado em JPA
        repository = new UsuarioDAO();
    }

    @AfterEach
    void tearDown() {
        // Sem limpeza de arquivo quando usamos JPA DAO; o banco é responsabilidade do ambiente de testes
    }

    @Test
    void testSalvarEBuscarPorId() {
        Usuario usuario = new Usuario("Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        Usuario salvo = repository.salvar(usuario);
        assertNotEquals(0, salvo.getId());
        Optional<Usuario> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Joao", buscado.get().getNome());
    }

    @Test
    void testBuscarPorEmail() {
        Usuario usuario = new Usuario("Maria", "maria@email.com", "senha456", TipoUsuario.COMUM);
        repository.salvar(usuario);
        Optional<Usuario> buscado = repository.buscarPorEmail("maria@email.com");
        assertTrue(buscado.isPresent());
        assertEquals("Maria", buscado.get().getNome());
    }

    @Test
    void testListarTodos() {
        repository.salvar(new Usuario("Joao", "joao@email.com", "senha123", TipoUsuario.COMUM));
        repository.salvar(new Usuario("Maria", "maria@email.com", "senha456", TipoUsuario.ADMIN));
        List<Usuario> usuarios = repository.listarTodos();
        assertTrue(usuarios.size() >= 2);
    }

    @Test
    void testEditar() {
        Usuario salvo = repository.salvar(new Usuario("Joao", "joao@email.com", "senha123", TipoUsuario.COMUM));
        int idSalvo = salvo.getId();
        salvo.setNome("Joao Silva");
        repository.editar(salvo);
        Optional<Usuario> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Joao Silva", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        Usuario salvo = repository.salvar(new Usuario("Joao", "joao@email.com", "senha123", TipoUsuario.COMUM));
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
        Optional<Usuario> buscado = repository.buscarPorEmail("inexistente@email.com");
        assertFalse(buscado.isPresent());
    }

    @Test
    void testEditarUsuarioInexistente() {
        Usuario usuario = new Usuario();
        usuario.setId(999);
        usuario.setNome("Nome");
        usuario.setEmail("email@test.com");
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
        Usuario usuario = new Usuario("Joao", "joao@email.com", "senha123", TipoUsuario.COMUM);
        Usuario salvo = repository.salvar(usuario);
        int idOriginal = salvo.getId();

        salvo.setNome("Joao Atualizado");
        Usuario atualizado = repository.salvar(salvo);

        assertEquals(idOriginal, atualizado.getId());
        assertEquals("Joao Atualizado", atualizado.getNome());
    }

    @Test
    void testGerarProximoId() {
        Usuario u = new Usuario("Temp", "temp@example.com", "s", TipoUsuario.COMUM);
        Usuario salvo = repository.salvar(u);
        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);
    }

    @Test
    void testPersistenciaEmArquivo() {
        Usuario usuario = new Usuario("Test", "test@email.com", "senha", TipoUsuario.COMUM);
        repository.salvar(usuario);

        IUsuarioRepository novoRepository = new UsuarioDAO();
        Optional<Usuario> recuperado = novoRepository.buscarPorEmail("test@email.com");

        assertTrue(recuperado.isPresent());
        assertEquals("Test", recuperado.get().getNome());
    }
}
