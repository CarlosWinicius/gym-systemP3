//package br.upe;
//
//import br.upe.data.beans.IndicadorBiomedico;
//import br.upe.data.interfaces.IIndicadorBiomedicoRepository;
//import br.upe.data.dao.IndicadorBiomedicoRepositoryImpl;
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
//class IndicadorBiomedicoRepositoryImplTest {
//
//    private IIndicadorBiomedicoRepository repository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/indicadores_test.csv";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//
//        repository = new IndicadorBiomedicoRepositoryImpl(TEST_CSV_PATH);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//    }
//
//    @Test
//    void testSalvarEBuscarPorId() {
//        IndicadorBiomedico indicador = IndicadorBiomedico.builder()
//                .idUsuario(1)
//                .data(LocalDate.now())
//                .pesoKg(80.0)
//                .alturaCm(175.0)
//                .percentualGordura(20.0)
//                .percentualMassaMagra(70.0)
//                .imc(26.1)
//                .build();
//
//        IndicadorBiomedico salvo = repository.salvar(indicador);
//
//        assertNotEquals(0, salvo.getId());
//
//        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(salvo.getId());
//        assertTrue(buscado.isPresent());
//        assertEquals(1, buscado.get().getIdUsuario());
//    }
//
//    @Test
//    void testListarPorUsuario() {
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(LocalDate.now().minusDays(1)).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(LocalDate.now()).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(2).data(LocalDate.now()).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//
//        List<IndicadorBiomedico> indicadoresUsuario1 = repository.listarPorUsuario(1);
//        assertEquals(2, indicadoresUsuario1.size());
//    }
//
//    @Test
//    void testBuscarPorPeriodo() {
//        LocalDate hoje = LocalDate.now();
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(hoje.minusDays(10)).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(hoje.minusDays(5)).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(hoje).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//
//        List<IndicadorBiomedico> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(6), hoje.plusDays(1));
//        assertEquals(2, resultado.size());
//    }
//
//    @Test
//    void testEditar() {
//        IndicadorBiomedico salvo = repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(LocalDate.now()).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        int idSalvo = salvo.getId();
//
//        salvo.setPesoKg(85.0);
//        repository.editar(salvo);
//
//        Optional<IndicadorBiomedico> editado = repository.buscarPorId(idSalvo);
//        assertTrue(editado.isPresent());
//        assertEquals(85.0, editado.get().getPesoKg());
//    }
//
//    @Test
//    void testDeletar() {
//        IndicadorBiomedico salvo = repository.salvar(IndicadorBiomedico.builder().idUsuario(1).data(LocalDate.now()).pesoKg(80.0).alturaCm(175.0).percentualGordura(20.0).percentualMassaMagra(70.0).imc(26.1).build());
//        int idSalvo = salvo.getId();
//
//        repository.deletar(idSalvo);
//
//        Optional<IndicadorBiomedico> depoisDeDeletar = repository.buscarPorId(idSalvo);
//        assertFalse(depoisDeDeletar.isPresent());
//    }
//
//    @Test
//    void testBuscarPorIdInexistente() {
//        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(999);
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testEditarIndicadorInexistente() {
//        IndicadorBiomedico indicador = IndicadorBiomedico.builder()
//                .id(999)
//                .idUsuario(1)
//                .data(LocalDate.now())
//                .pesoKg(80.0)
//                .alturaCm(175.0)
//                .percentualGordura(20.0)
//                .percentualMassaMagra(70.0)
//                .imc(26.1)
//                .build();
//        repository.editar(indicador);
//        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(999);
//        assertFalse(buscado.isPresent());
//    }
//
//    @Test
//    void testDeletarIndicadorInexistente() {
//        repository.deletar(999);
//        assertTrue(repository.listarPorUsuario(1).isEmpty());
//    }
//
//    @Test
//    void testGerarProximoId() {
//        int proximoId = repository.gerarProximoId();
//        assertTrue(proximoId > 0);
//    }
//
//    @Test
//    void testListarTodos() {
//        repository.salvar(IndicadorBiomedico.builder()
//                .idUsuario(1)
//                .data(LocalDate.now())
//                .pesoKg(80.0)
//                .alturaCm(175.0)
//                .percentualGordura(20.0)
//                .percentualMassaMagra(70.0)
//                .imc(26.1)
//                .build());
//        repository.salvar(IndicadorBiomedico.builder()
//                .idUsuario(2)
//                .data(LocalDate.now())
//                .pesoKg(80.0)
//                .alturaCm(175.0)
//                .percentualGordura(20.0)
//                .percentualMassaMagra(70.0)
//                .imc(26.1)
//                .build());
//
//        List<IndicadorBiomedico> todos = repository.listarTodos();
//        assertEquals(2, todos.size());
//    }
//
//    @Test
//    void testPersistenciaEmArquivo() {
//        IndicadorBiomedico indicador = IndicadorBiomedico.builder()
//                .idUsuario(1)
//                .data(LocalDate.of(2025, 1, 1))
//                .pesoKg(75.5)
//                .alturaCm(180.0)
//                .percentualGordura(18.0)
//                .percentualMassaMagra(72.0)
//                .imc(23.3)
//                .build();
//        repository.salvar(indicador);
//
//        IIndicadorBiomedicoRepository novoRepository = new IndicadorBiomedicoRepositoryImpl(TEST_CSV_PATH);
//        List<IndicadorBiomedico> recuperados = novoRepository.listarPorUsuario(1);
//
//        assertFalse(recuperados.isEmpty());
//        assertEquals(75.5, recuperados.get(0).getPesoKg());
//    }
//}
