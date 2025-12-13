package br.upe;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.dao.ExercicioDAO;
import br.upe.data.dao.UsuarioDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioRepositoryImplTest {

    private IExercicioRepository repository;
    private int userId1;
    private int userId2;

    @BeforeEach
    void setUp() {
        // Usando DAO JPA
        repository = new ExercicioDAO();

        // Persistir usuários necessários para os testes
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Usuario u1 = new Usuario();
        u1.setNome("Usuario 1");
        u1.setEmail("user1+" + System.nanoTime() + "@example.com");
        u1.setSenha("senha1");
        u1.setTipo(TipoUsuario.COMUM); // Inicialize o campo 'tipo' com um valor válido
        userId1 = usuarioDAO.salvar(u1).getId();

        Usuario u2 = new Usuario();
        u2.setNome("Usuario 2");
        u2.setEmail("user2+" + System.nanoTime() + "@example.com");
        u2.setSenha("senha2");
        u2.setTipo(TipoUsuario.ADMIN); // Inicialize o campo 'tipo' com um valor válido
        userId2 = usuarioDAO.salvar(u2).getId();
    }


    @AfterEach
    void tearDown() {
        for (int userId = 1; userId <= 10; userId++) {
            List<Exercicio> lista = repository.buscarTodosDoUsuario(userId);
            for (Exercicio e : lista) {
                repository.deletar(e.getId());
            }
        }
    }

    // Helper para criar Exercicio (entidade)
    private Exercicio createExercicio(int usuarioId, String nome, String descricao, String caminhoGif) {
        Usuario u = new Usuario();
        u.setId(usuarioId);
        Exercicio e = new Exercicio();
        e.setUsuario(u);
        e.setNome(nome);
        e.setDescricao(descricao);
        e.setCaminhoGif(caminhoGif);
        return e;
    }

    @Test
    void testSalvarEBuscarPorId() {
        Exercicio exercicio = createExercicio(userId1, "Exercicio 1", "Descricao", "gif");
        Exercicio salvo = repository.salvar(exercicio);

        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);

        Optional<Exercicio> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Exercicio 1", buscado.get().getNome());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        repository.salvar(createExercicio(userId1, "Ex1", "Desc1", "gif1"));
        repository.salvar(createExercicio(userId1, "Ex2", "Desc2", "gif2"));
        repository.salvar(createExercicio(userId2, "Ex3", "Desc3", "gif3"));

        List<Exercicio> exerciciosUsuario1 = repository.buscarTodosDoUsuario(userId1);
        assertEquals(2, exerciciosUsuario1.size());
    }

    @Test
    void testEditar() {
        Exercicio salvo = repository.salvar(createExercicio(userId1, "Ex1", "Desc1", "gif1"));
        Integer idSalvo = salvo.getId();

        salvo.setNome("Ex1 Editado");
        repository.editar(salvo);

        Optional<Exercicio> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Ex1 Editado", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        Exercicio salvo = repository.salvar(createExercicio(userId1, "Ex1", "Desc1", "gif1"));
        Integer idSalvo = salvo.getId();

        repository.deletar(idSalvo);

        Optional<Exercicio> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorNome() {
        repository.salvar(createExercicio(userId1, "Ex1", "Desc1", "gif1"));

        // Certifique-se de que não há duplicatas
        List<Exercicio> duplicados = repository.buscarTodosDoUsuario(userId1);
        assertEquals(1, duplicados.stream().filter(e -> "Ex1".equals(e.getNome())).count());

        Optional<Exercicio> buscado = repository.buscarPorNome("Ex1");
        assertTrue(buscado.isPresent());
        assertEquals("Ex1", buscado.get().getNome());
    }


    @Test
    void testBuscarPorNomeInexistente() {
        Optional<Exercicio> buscado = repository.buscarPorNome("Inexistente");
        assertFalse(buscado.isPresent());
    }

    @Test
    void testBuscarPorIdInexistente() {
        Optional<Exercicio> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testEditarExercicioInexistente() {
        Exercicio exercicio = createExercicio(userId1, "Ex", "Desc", "gif");
        exercicio.setId(999);
        repository.editar(exercicio);
        Optional<Exercicio> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarExercicioInexistente() {
        repository.deletar(999);
        assertTrue(repository.buscarTodosDoUsuario(userId1).isEmpty());
    }

    @Test
    void testProximoId() {
        Exercicio ex1 = repository.salvar(createExercicio(userId1, "Ex1", "Desc1", "gif1"));
        Exercicio ex2 = repository.salvar(createExercicio(userId1, "Ex2", "Desc2", "gif2"));

        assertEquals(ex1.getId() + 1, ex2.getId());
    }
/*
//teste de persistência em arquivo removido, pois o DAO atual utiliza JPA para persistência em banco de dados.
    @Test
    void testPersistenciaEmArquivo() {
        Exercicio exercicio = createExercicio(userId1, "teste", "TestDesc", "testgif");
        repository.salvar(exercicio);

        IExercicioRepository novoRepository = new ExercicioDAO();
        List<Exercicio> recuperados = novoRepository.buscarTodosDoUsuario(userId1);

        assertFalse(recuperados.isEmpty());
        assertTrue(recuperados.stream().anyMatch(e -> "teste".equals(e.getNome())));
    }*/
}
