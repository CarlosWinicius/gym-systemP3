package br.upe;

import br.upe.data.beans.Exercicio;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioRepositoryImplTest {

    private IExercicioRepository repository;
    private static final String TEST_CSV_PATH = "src/test/resources/data/exercicios_test.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("src/test/resources/data"));
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));

        repository = new ExercicioRepositoryImpl(TEST_CSV_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
    }

    @Test
    void testSalvarEBuscarPorId() {
        Exercicio exercicio = new Exercicio(0, 1, "Exercicio 1", "Descricao", "gif");
        Exercicio salvo = repository.salvar(exercicio);

        assertNotEquals(0, salvo.getIdExercicio());

        Optional<Exercicio> buscado = repository.buscarPorId(salvo.getIdExercicio());
        assertTrue(buscado.isPresent());
        assertEquals("Exercicio 1", buscado.get().getNome());
    }

    @Test
    void testBuscarTodosDoUsuario() {
        repository.salvar(new Exercicio(0, 1, "Ex1", "Desc1", "gif1"));
        repository.salvar(new Exercicio(0, 1, "Ex2", "Desc2", "gif2"));
        repository.salvar(new Exercicio(0, 2, "Ex3", "Desc3", "gif3"));

        List<Exercicio> exerciciosUsuario1 = repository.buscarTodosDoUsuario(1);
        assertEquals(2, exerciciosUsuario1.size());
    }

    @Test
    void testEditar() {
        Exercicio salvo = repository.salvar(new Exercicio(0, 1, "Ex1", "Desc1", "gif1"));
        int idSalvo = salvo.getIdExercicio();

        salvo.setNome("Ex1 Editado");
        repository.editar(salvo);

        Optional<Exercicio> editado = repository.buscarPorId(idSalvo);
        assertTrue(editado.isPresent());
        assertEquals("Ex1 Editado", editado.get().getNome());
    }

    @Test
    void testDeletar() {
        Exercicio salvo = repository.salvar(new Exercicio(0, 1, "Ex1", "Desc1", "gif1"));
        int idSalvo = salvo.getIdExercicio();

        repository.deletar(idSalvo);

        Optional<Exercicio> depoisDeDeletar = repository.buscarPorId(idSalvo);
        assertFalse(depoisDeDeletar.isPresent());
    }

    @Test
    void testBuscarPorNome() {
        repository.salvar(new Exercicio(0, 1, "Ex1", "Desc1", "gif1"));

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
        Exercicio exercicio = new Exercicio(999, 1, "Ex", "Desc", "gif");
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
        int proximoId = repository.proximoId();
        assertTrue(proximoId > 0);
    }

    @Test
    void testPersistenciaEmArquivo() {
        Exercicio exercicio = new Exercicio(0, 1, "TestEx", "TestDesc", "testgif");
        repository.salvar(exercicio);

        IExercicioRepository novoRepository = new ExercicioRepositoryImpl(TEST_CSV_PATH);
        Optional<Exercicio> recuperado = novoRepository.buscarPorNome("TestEx");

        assertTrue(recuperado.isPresent());
        assertEquals("TestDesc", recuperado.get().getDescricao());
    }
}
