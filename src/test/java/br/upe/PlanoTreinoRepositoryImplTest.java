//package br.upe;
//
//import br.upe.data.beans.ItemPlanoTreino;
//import br.upe.data.beans.PlanoTreino;
//import br.upe.data.repository.IPlanoTreinoRepository;
//import br.upe.data.repository.impl.PlanoTreinoRepositoryImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PlanoTreinoRepositoryImplTest {
//
//    private IPlanoTreinoRepository repository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/planos_treino_test.csv";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//
//        repository = new PlanoTreinoRepositoryImpl(TEST_CSV_PATH);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//    }
//
//    @Test
//    void testSalvarEBuscarPorId() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano 1");
//        plano.adicionarItem(new ItemPlanoTreino(1, 50, 10));
//
//        PlanoTreino salvo = repository.salvar(plano);
//
//        assertNotEquals(0, salvo.getIdPlano());
//
//        Optional<PlanoTreino> buscado = repository.buscarPorId(salvo.getIdPlano());
//        assertTrue(buscado.isPresent());
//        assertEquals("Plano 1", buscado.get().getNome());
//    }
//
//    @Test
//    void testBuscarTodosDoUsuario() {
//        PlanoTreino plano1 = new PlanoTreino(1, "Plano 1");
//        PlanoTreino plano2 = new PlanoTreino(1, "Plano 2");
//        PlanoTreino plano3 = new PlanoTreino(2, "Plano 3");
//
//        repository.salvar(plano1);
//        repository.salvar(plano2);
//        repository.salvar(plano3);
//
//        List<PlanoTreino> planosUsuario1 = repository.buscarTodosDoUsuario(1);
//        assertEquals(2, planosUsuario1.size());
//    }
//
//    @Test
//    void testEditar() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano 1");
//        PlanoTreino salvo = repository.salvar(plano);
//        int idSalvo = salvo.getIdPlano();
//
//        salvo.setNome("Plano 1 Editado");
//        repository.editar(salvo);
//
//        Optional<PlanoTreino> editado = repository.buscarPorId(idSalvo);
//        assertTrue(editado.isPresent());
//        assertEquals("Plano 1 Editado", editado.get().getNome());
//    }
//
//    @Test
//    void testDeletar() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano 1");
//        PlanoTreino salvo = repository.salvar(plano);
//        int idSalvo = salvo.getIdPlano();
//
//        repository.deletar(idSalvo);
//
//        Optional<PlanoTreino> depoisDeDeletar = repository.buscarPorId(idSalvo);
//        assertFalse(depoisDeDeletar.isPresent());
//    }
//
//    @Test
//    void testBuscarPorNomeEUsuario() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano 1");
//        repository.salvar(plano);
//
//        Optional<PlanoTreino> buscado = repository.buscarPorNomeEUsuario(1, "Plano 1");
//        assertTrue(buscado.isPresent());
//        assertEquals("Plano 1", buscado.get().getNome());
//    }
//
//    @Test
//    void testBuscarPorNomeEUsuarioInexistente() {
//        Optional<PlanoTreino> buscado = repository.buscarPorNomeEUsuario(1, "Inexistente");
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testBuscarPorIdInexistente() {
//        Optional<PlanoTreino> buscado = repository.buscarPorId(999);
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testEditarPlanoInexistente() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano");
//        plano.setIdPlano(999);
//        repository.editar(plano);
//        Optional<PlanoTreino> buscado = repository.buscarPorId(999);
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testDeletarPlanoInexistente() {
//        repository.deletar(999);
//        assertTrue(repository.buscarTodosDoUsuario(1).isEmpty());
//    }
//
//    @Test
//    void testAtualizar() {
//        PlanoTreino plano = new PlanoTreino(1, "Plano 1");
//        PlanoTreino salvo = repository.salvar(plano);
//
//        salvo.setNome("Plano Atualizado");
//        repository.atualizar(salvo);
//
//        Optional<PlanoTreino> atualizado = repository.buscarPorId(salvo.getIdPlano());
//        assertTrue(atualizado.isPresent());
//        assertEquals("Plano Atualizado", atualizado.get().getNome());
//    }
//
//    @Test
//    void testProximoId() {
//        int proximoId = repository.proximoId();
//        assertTrue(proximoId > 0);
//    }
//
//    @Test
//    void testPersistenciaEmArquivo() {
//        PlanoTreino plano = new PlanoTreino(1, "TestPlano");
//        plano.adicionarItem(new ItemPlanoTreino(1, 100, 15));
//        repository.salvar(plano);
//
//        IPlanoTreinoRepository novoRepository = new PlanoTreinoRepositoryImpl(TEST_CSV_PATH);
//        Optional<PlanoTreino> recuperado = novoRepository.buscarPorNomeEUsuario(1, "TestPlano");
//
//        assertTrue(recuperado.isPresent());
//        assertEquals(1, recuperado.get().getItensTreino().size());
//        assertEquals(100, recuperado.get().getItensTreino().get(0).getCargaKg());
//    }
//}
