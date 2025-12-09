//package br.upe;
//
//import br.upe.data.beans.ItemSessaoTreino;
//import br.upe.data.beans.SessaoTreino;
//import br.upe.data.interfaces.ISessaoTreinoRepository;
//import br.upe.data.dao.SessaoTreinoRepositoryImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class SessaoTreinoRepositoryImplTest {
//
//    private ISessaoTreinoRepository repository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/sessoes_treino_test.csv";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//
//        repository = new SessaoTreinoRepositoryImpl(TEST_CSV_PATH);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//    }
//
//    @Test
//    void testSalvarEBuscarPorId() {
//        SessaoTreino sessao = new SessaoTreino(1, 1);
//        sessao.setDataSessao(LocalDate.now());
//        sessao.adicionarItemExecutado(new ItemSessaoTreino(1, 10, 50.0));
//
//        SessaoTreino salvo = repository.salvar(sessao);
//
//        assertNotEquals(0, salvo.getIdSessao());
//
//        Optional<SessaoTreino> buscado = repository.buscarPorId(salvo.getIdSessao());
//        assertTrue(buscado.isPresent());
//        assertEquals(1, buscado.get().getIdUsuario());
//    }
//
//    @Test
//    void testBuscarTodosDoUsuario() {
//        SessaoTreino sessao1 = new SessaoTreino(1, 1);
//        sessao1.setDataSessao(LocalDate.now().minusDays(1));
//        SessaoTreino sessao2 = new SessaoTreino(1, 1);
//        sessao2.setDataSessao(LocalDate.now());
//        SessaoTreino sessao3 = new SessaoTreino(2, 1);
//        sessao3.setDataSessao(LocalDate.now());
//
//        repository.salvar(sessao1);
//        repository.salvar(sessao2);
//        repository.salvar(sessao3);
//
//        List<SessaoTreino> sessoesUsuario1 = repository.buscarTodosDoUsuario(1);
//        assertEquals(2, sessoesUsuario1.size());
//    }
//
//    @Test
//    void testBuscarPorPeriodo() {
//        LocalDate hoje = LocalDate.now();
//        SessaoTreino s1 = new SessaoTreino(1, 1);
//        s1.setDataSessao(hoje.minusDays(10));
//        SessaoTreino s2 = new SessaoTreino(1, 1);
//        s2.setDataSessao(hoje.minusDays(5));
//        SessaoTreino s3 = new SessaoTreino(1, 1);
//        s3.setDataSessao(hoje);
//
//        repository.salvar(s1);
//        repository.salvar(s2);
//        repository.salvar(s3);
//
//        List<SessaoTreino> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(6), hoje.plusDays(1));
//        assertEquals(2, resultado.size());
//    }
//
//    @Test
//    void testEditar() {
//        SessaoTreino sessao = new SessaoTreino(1, 1);
//        sessao.setDataSessao(LocalDate.now());
//        SessaoTreino salvo = repository.salvar(sessao);
//        int idSalvo = salvo.getIdSessao();
//
//        salvo.setDataSessao(LocalDate.now().plusDays(1));
//        repository.editar(salvo);
//
//        Optional<SessaoTreino> editado = repository.buscarPorId(idSalvo);
//        assertTrue(editado.isPresent());
//        assertEquals(LocalDate.now().plusDays(1), editado.get().getDataSessao());
//    }
//
//    @Test
//    void testDeletar() {
//        SessaoTreino sessao = new SessaoTreino(1, 1);
//        sessao.setDataSessao(LocalDate.now());
//        SessaoTreino salvo = repository.salvar(sessao);
//        int idSalvo = salvo.getIdSessao();
//
//        repository.deletar(idSalvo);
//
//        Optional<SessaoTreino> depoisDeDeletar = repository.buscarPorId(idSalvo);
//        assertFalse(depoisDeDeletar.isPresent());
//    }
//
//    @Test
//    void testBuscarPorIdInexistente() {
//        Optional<SessaoTreino> buscado = repository.buscarPorId(999);
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testDeletarSessaoInexistente() {
//        repository.deletar(999);
//        assertTrue(repository.buscarTodosDoUsuario(1).isEmpty());
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
//        SessaoTreino sessao = new SessaoTreino(1, 1);
//        sessao.setDataSessao(LocalDate.of(2025, 1, 15));
//        sessao.adicionarItemExecutado(new ItemSessaoTreino(1, 12, 60.0));
//        repository.salvar(sessao);
//
//        ISessaoTreinoRepository novoRepository = new SessaoTreinoRepositoryImpl(TEST_CSV_PATH);
//        List<SessaoTreino> recuperadas = novoRepository.buscarTodosDoUsuario(1);
//
//        assertFalse(recuperadas.isEmpty());
//        assertEquals(LocalDate.of(2025, 1, 15), recuperadas.get(0).getDataSessao());
//        assertEquals(1, recuperadas.get(0).getItensExecutados().size());
//        assertEquals(60.0, recuperadas.get(0).getItensExecutados().get(0).getCargaRealizada());
//    }
//
//    @Test
//    void testBuscarPorPeriodoVazio() {
//        LocalDate hoje = LocalDate.now();
//        List<SessaoTreino> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(7), hoje.minusDays(1));
//        assertTrue(resultado.isEmpty());
//    }
//}
