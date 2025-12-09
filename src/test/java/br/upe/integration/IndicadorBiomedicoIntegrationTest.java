//package br.upe.integration;
//
//import br.upe.controller.business.IndicadorBiomedicoService;
//import br.upe.controller.business.RelatorioDiferencaIndicadores;
//import br.upe.data.beans.IndicadorBiomedico;
//import br.upe.data.repository.IIndicadorBiomedicoRepository;
//import br.upe.data.repository.impl.IndicadorBiomedicoRepositoryImpl;
//import org.junit.jupiter.api.*;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Testes de integração para o fluxo completo de indicadores biomédicos
// * Integra IndicadorBiomedicoService + IndicadorBiomedicoRepositoryImpl + arquivo CSV
// */
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class IndicadorBiomedicoIntegrationTest {
//
//    private IndicadorBiomedicoService indicadorService;
//    private IIndicadorBiomedicoRepository indicadorRepository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/indicadores_integration_test.csv";
//    private static final int ID_USUARIO_TESTE = 1;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//        indicadorRepository = new IndicadorBiomedicoRepositoryImpl(TEST_CSV_PATH);
//        indicadorService = new IndicadorBiomedicoService(indicadorRepository);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("Integração: Deve cadastrar indicador biomédico e calcular IMC automaticamente")
//    void testCadastrarIndicadorComIMC() {
//        // Dado
//        LocalDate data = LocalDate.of(2024, 1, 1);
//        double peso = 80.0;
//        double altura = 175.0;
//        double gordura = 15.0;
//        double massaMagra = 85.0;
//
//        // Quando
//        IndicadorBiomedico indicador = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, data, peso, altura, gordura, massaMagra
//        );
//
//        // Então
//        assertNotNull(indicador);
//        assertNotEquals(0, indicador.getId());
//        assertEquals(peso, indicador.getPesoKg());
//        assertEquals(altura, indicador.getAlturaCm());
//        assertEquals(gordura, indicador.getPercentualGordura());
//        assertEquals(massaMagra, indicador.getPercentualMassaMagra());
//
//        // Verifica cálculo do IMC (peso / (altura/100)^2)
//        double imcEsperado = peso / Math.pow(altura / 100, 2);
//        assertEquals(imcEsperado, indicador.getImc(), 0.01);
//
//        // Verifica persistência
//        List<IndicadorBiomedico> indicadores = indicadorRepository.listarPorUsuario(ID_USUARIO_TESTE);
//        assertEquals(1, indicadores.size());
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("Integração: Deve usar data atual quando data não for informada")
//    void testCadastrarIndicadorSemData() {
//        IndicadorBiomedico indicador = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, null, 75.0, 170.0, 18.0, 82.0
//        );
//
//        assertNotNull(indicador);
//        assertEquals(LocalDate.now(), indicador.getData());
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("Integração: Deve validar valores de peso e altura")
//    void testValidacaoPesoAltura() {
//        LocalDate data = LocalDate.now();
//
//        // Peso zero
//        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data, 0, 170.0, 15.0, 85.0);
//        });
//        assertTrue(exception1.getMessage().contains("Peso e altura devem ser maiores que zero"));
//
//        // Altura negativa
//        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data, 70.0, -170.0, 15.0, 85.0);
//        });
//        assertTrue(exception2.getMessage().contains("Peso e altura devem ser maiores que zero"));
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("Integração: Deve validar percentuais não negativos")
//    void testValidacaoPercentuais() {
//        LocalDate data = LocalDate.now();
//
//        // Percentual de gordura negativo
//        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data, 75.0, 170.0, -5.0, 85.0);
//        });
//        assertTrue(exception1.getMessage().contains("Percentuais de gordura e massa magra não podem ser negativos"));
//
//        // Percentual de massa magra negativo
//        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data, 75.0, 170.0, 15.0, -85.0);
//        });
//        assertTrue(exception2.getMessage().contains("Percentuais de gordura e massa magra não podem ser negativos"));
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("Integração: Deve listar todos os indicadores do usuário")
//    void testListarIndicadoresDoUsuario() {
//        // Cadastra indicadores para usuário 1
//        indicadorService.cadastrarIndicador(1, LocalDate.of(2024, 1, 1), 80.0, 175.0, 15.0, 85.0);
//        indicadorService.cadastrarIndicador(1, LocalDate.of(2024, 2, 1), 78.0, 175.0, 14.0, 86.0);
//        indicadorService.cadastrarIndicador(1, LocalDate.of(2024, 3, 1), 76.0, 175.0, 13.0, 87.0);
//
//        // Cadastra indicador para usuário 2
//        indicadorService.cadastrarIndicador(2, LocalDate.of(2024, 1, 1), 70.0, 165.0, 20.0, 80.0);
//
//        // Lista indicadores do usuário 1
//        List<IndicadorBiomedico> indicadoresUser1 = indicadorService.listarTodosDoUsuario(1);
//        assertEquals(3, indicadoresUser1.size());
//        assertTrue(indicadoresUser1.stream().allMatch(i -> i.getIdUsuario() == 1));
//
//        // Lista indicadores do usuário 2
//        List<IndicadorBiomedico> indicadoresUser2 = indicadorService.listarTodosDoUsuario(2);
//        assertEquals(1, indicadoresUser2.size());
//        assertEquals(2, indicadoresUser2.get(0).getIdUsuario());
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("Integração: Deve gerar relatório por período")
//    void testGerarRelatorioPorPeriodo() {
//        // Cadastra indicadores em diferentes datas
//        LocalDate data1 = LocalDate.of(2024, 1, 1);
//        LocalDate data2 = LocalDate.of(2024, 1, 15);
//        LocalDate data3 = LocalDate.of(2024, 2, 1);
//        LocalDate data4 = LocalDate.of(2024, 3, 1);
//
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data1, 80.0, 175.0, 15.0, 85.0);
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data2, 79.0, 175.0, 14.5, 85.5);
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data3, 78.0, 175.0, 14.0, 86.0);
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, data4, 77.0, 175.0, 13.5, 86.5);
//
//        // Gera relatório para janeiro e fevereiro
//        LocalDate inicio = LocalDate.of(2024, 1, 1);
//        LocalDate fim = LocalDate.of(2024, 2, 28);
//
//        List<IndicadorBiomedico> relatorio = indicadorService.gerarRelatorioPorData(
//                ID_USUARIO_TESTE, inicio, fim
//        );
//
//        // Deve conter apenas os indicadores do período
//        assertEquals(3, relatorio.size());
//        assertTrue(relatorio.stream().allMatch(i ->
//                !i.getData().isBefore(inicio) && !i.getData().isAfter(fim)
//        ));
//
//        // Verifica ordenação por data
//        for (int i = 1; i < relatorio.size(); i++) {
//            assertTrue(relatorio.get(i).getData().isAfter(relatorio.get(i-1).getData()) ||
//                      relatorio.get(i).getData().equals(relatorio.get(i-1).getData()));
//        }
//    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Integração: Deve gerar relatório de diferença entre indicadores")
//    void testGerarRelatorioDiferenca() {
//        // Cadastra indicador inicial
//        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataInicio, 85.0, 175.0, 18.0, 82.0);
//
//        // Cadastra indicadores intermediários
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, LocalDate.of(2024, 2, 1), 83.0, 175.0, 17.0, 83.0);
//
//        // Cadastra indicador final
//        LocalDate dataFim = LocalDate.of(2024, 3, 1);
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataFim, 80.0, 175.0, 15.0, 85.0);
//
//        // Gera relatório de diferença
//        RelatorioDiferencaIndicadores relatorio = indicadorService.gerarRelatorioDiferenca(
//                ID_USUARIO_TESTE, dataInicio, dataFim
//        );
//
//        // Verifica
//        assertNotNull(relatorio);
//        assertTrue(relatorio.getIndicadorInicial().isPresent());
//        assertTrue(relatorio.getIndicadorFinal().isPresent());
//
//        // Verifica datas
//        assertEquals(dataInicio, relatorio.getIndicadorInicial().get().getData());
//        assertEquals(dataFim, relatorio.getIndicadorFinal().get().getData());
//
//        // Verifica valores
//        assertEquals(85.0, relatorio.getIndicadorInicial().get().getPesoKg());
//        assertEquals(80.0, relatorio.getIndicadorFinal().get().getPesoKg());
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("Integração: Deve validar datas no relatório por período")
//    void testValidacaoDatasRelatorio() {
//        LocalDate dataInicio = LocalDate.of(2024, 3, 1);
//        LocalDate dataFim = LocalDate.of(2024, 1, 1);
//
//        // Data início após data fim
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.gerarRelatorioPorData(ID_USUARIO_TESTE, dataInicio, dataFim);
//        });
//        assertTrue(exception.getMessage().contains("Data de início não pode ser posterior à data de fim"));
//
//        // Datas nulas
//        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.gerarRelatorioPorData(ID_USUARIO_TESTE, null, dataFim);
//        });
//        assertTrue(exception2.getMessage().contains("Datas de início e fim não podem ser nulas"));
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("Integração: Fluxo completo - evolução de indicadores ao longo do tempo")
//    void testFluxoCompletoEvolucaoIndicadores() {
//        // Simula 6 meses de acompanhamento
//        LocalDate dataBase = LocalDate.of(2024, 1, 1);
//
//        // Janeiro - início
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase, 90.0, 175.0, 25.0, 75.0);
//
//        // Fevereiro
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase.plusMonths(1), 87.0, 175.0, 23.0, 77.0);
//
//        // Março
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase.plusMonths(2), 85.0, 175.0, 21.0, 79.0);
//
//        // Abril
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase.plusMonths(3), 82.0, 175.0, 19.0, 81.0);
//
//        // Maio
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase.plusMonths(4), 80.0, 175.0, 17.0, 83.0);
//
//        // Junho
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, dataBase.plusMonths(5), 78.0, 175.0, 15.0, 85.0);
//
//        // Lista todos os indicadores
//        List<IndicadorBiomedico> todosIndicadores = indicadorService.listarTodosDoUsuario(ID_USUARIO_TESTE);
//        assertEquals(6, todosIndicadores.size());
//
//        // Verifica relatório do período total
//        List<IndicadorBiomedico> relatorioCompleto = indicadorService.gerarRelatorioPorData(
//                ID_USUARIO_TESTE, dataBase, dataBase.plusMonths(5)
//        );
//        assertEquals(6, relatorioCompleto.size());
//
//        // Verifica diferença total
//        RelatorioDiferencaIndicadores diferenca = indicadorService.gerarRelatorioDiferenca(
//                ID_USUARIO_TESTE, dataBase, dataBase.plusMonths(5)
//        );
//
//        assertTrue(diferenca.getIndicadorInicial().isPresent());
//        assertTrue(diferenca.getIndicadorFinal().isPresent());
//
//        // Verifica perda de peso
//        double pesoInicial = diferenca.getIndicadorInicial().get().getPesoKg();
//        double pesoFinal = diferenca.getIndicadorFinal().get().getPesoKg();
//        assertTrue(pesoInicial > pesoFinal, "Deve ter havido perda de peso");
//        assertEquals(90.0, pesoInicial);
//        assertEquals(78.0, pesoFinal);
//
//        // Verifica redução de gordura
//        double gorduraInicial = diferenca.getIndicadorInicial().get().getPercentualGordura();
//        double gorduraFinal = diferenca.getIndicadorFinal().get().getPercentualGordura();
//        assertTrue(gorduraInicial > gorduraFinal, "Deve ter havido redução de gordura");
//
//        // Verifica aumento de massa magra
//        double massaMagraInicial = diferenca.getIndicadorInicial().get().getPercentualMassaMagra();
//        double massaMagraFinal = diferenca.getIndicadorFinal().get().getPercentualMassaMagra();
//        assertTrue(massaMagraFinal > massaMagraInicial, "Deve ter havido ganho de massa magra");
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("Integração: Deve calcular IMC corretamente para diferentes valores")
//    void testCalculoIMCVariosValores() {
//        // Teste 1: IMC normal
//        IndicadorBiomedico ind1 = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, LocalDate.now(), 70.0, 170.0, 15.0, 85.0
//        );
//        double imcEsperado1 = 70.0 / Math.pow(1.70, 2);
//        assertEquals(imcEsperado1, ind1.getImc(), 0.01);
//
//        // Teste 2: IMC sobrepeso
//        IndicadorBiomedico ind2 = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, LocalDate.now(), 85.0, 170.0, 20.0, 80.0
//        );
//        double imcEsperado2 = 85.0 / Math.pow(1.70, 2);
//        assertEquals(imcEsperado2, ind2.getImc(), 0.01);
//
//        // Teste 3: IMC baixo peso
//        IndicadorBiomedico ind3 = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, LocalDate.now(), 55.0, 175.0, 10.0, 90.0
//        );
//        double imcEsperado3 = 55.0 / Math.pow(1.75, 2);
//        assertEquals(imcEsperado3, ind3.getImc(), 0.01);
//    }
//
//    @Test
//    @Order(11)
//    @DisplayName("Integração: Deve retornar lista vazia para período sem indicadores")
//    void testRelatorioPeriodoVazio() {
//        // Cadastra indicador em janeiro
//        indicadorService.cadastrarIndicador(ID_USUARIO_TESTE, LocalDate.of(2024, 1, 15), 80.0, 175.0, 15.0, 85.0);
//
//        // Busca relatório em março (sem dados)
//        List<IndicadorBiomedico> relatorio = indicadorService.gerarRelatorioPorData(
//                ID_USUARIO_TESTE,
//                LocalDate.of(2024, 3, 1),
//                LocalDate.of(2024, 3, 31)
//        );
//
//        assertNotNull(relatorio);
//        assertTrue(relatorio.isEmpty());
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("Integração: Deve permitir indicadores na mesma data")
//    void testIndicadoresMesmaData() {
//        LocalDate data = LocalDate.of(2024, 1, 1);
//
//        // Cadastra dois indicadores na mesma data (ex: manhã e noite)
//        IndicadorBiomedico ind1 = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, data, 80.0, 175.0, 15.0, 85.0
//        );
//        IndicadorBiomedico ind2 = indicadorService.cadastrarIndicador(
//                ID_USUARIO_TESTE, data, 80.5, 175.0, 15.2, 84.8
//        );
//
//        assertNotNull(ind1);
//        assertNotNull(ind2);
//        assertNotEquals(ind1.getId(), ind2.getId());
//
//        // Verifica que ambos foram persistidos
//        List<IndicadorBiomedico> indicadores = indicadorService.listarTodosDoUsuario(ID_USUARIO_TESTE);
//        assertEquals(2, indicadores.size());
//    }
//}
//
