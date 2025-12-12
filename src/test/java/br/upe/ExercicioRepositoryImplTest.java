package br.upe;

import br.upe.data.entities.Exercicio;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.dao.ExercicioDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioRepositoryImplTest {

    private IExercicioRepository repository;

    @BeforeEach
    void setUp() {
        // Usando DAO JPA
        repository = new ExercicioDAO();
    }

    @AfterEach
    void tearDown() {
        // limpeza do banco de testes é responsabilidade do ambiente
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
        Exercicio exercicio = createExercicio(1, "Exercicio 1", "Descricao", "gif");
        Exercicio salvo = repository.salvar(exercicio);

        assertNotNull(salvo.getId());
        assertTrue(salvo.getId() > 0);

        Optional<Exercicio> buscado = repository.buscarPorId(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Exercicio 1", buscado.get().getNome());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        repository.salvar(createExercicio(1, "Ex1", "Desc1", "gif1"));
        repository.salvar(createExercicio(1, "Ex2", "Desc2", "gif2"));
        repository.salvar(createExercicio(2, "Ex3", "Desc3", "gif3"));

        List<Exercicio> exerciciosUsuario1 = repository.buscarTodosDoUsuario(1);
        assertEquals(2, exerciciosUsuario1.size());
    }

    @Test
    void testEditar() {
        Exercicio salvo = repository.salvar(createExercicio(1, "Ex1", "Desc1", "gif1"));
        Integer idSalvo = salvo.getId();

        salvo.setNome("Ex1 Editado");
        repository.editar(salvo);

        Optional<Exercicio> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Ex1 Editado", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        Exercicio salvo = repository.salvar(createExercicio(1, "Ex1", "Desc1", "gif1"));
        Integer idSalvo = salvo.getId();

        repository.deletar(idSalvo);

        Optional<Exercicio> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorNome() {
        repository.salvar(createExercicio(1, "Ex1", "Desc1", "gif1"));

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
        Exercicio exercicio = createExercicio(1, "Ex", "Desc", "gif");
        exercicio.setId(999);
        repository.editar(exercicio);
        Optional<Exercicio> buscado = repository.buscarPorId(999);
        assertFalse(buscado.isPresent());
    }

    @Test
    void testDeletarExercicioInexistente() {
        repository.deletar(999);
        assertTrue(repository.buscarTodosDoUsuario(1).isEmpty());
    }

    @Test
    void testProximoId() {
        Exercicio ex1 = repository.salvar(createExercicio(1, "Ex1", "Desc1", "gif1"));
        Exercicio ex2 = repository.salvar(createExercicio(1, "Ex2", "Desc2", "gif2"));

        assertEquals(ex1.getId() + 1, ex2.getId());
    }

    @Test
    void testPersistenciaEmArquivo() {
        Exercicio exercicio = createExercicio(1, "TestEx", "TestDesc", "testgif");
        repository.salvar(exercicio);

        IExercicioRepository novoRepository = new ExercicioDAO();
        Optional<Exercicio> recuperado = novoRepository.buscarPorNome("TestEx");

        assertTrue(recuperado.isPresent());
        assertEquals("TestDesc", recuperado.get().getDescricao());
    }
}
