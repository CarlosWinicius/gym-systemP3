//package br.upe.controller.business;
//
//import br.upe.data.beans.IndicadorBiomedico;
//import br.upe.data.repository.IIndicadorBiomedicoRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class IndicadorBiomedicoServiceTest {
//
//    @Mock
//    private IIndicadorBiomedicoRepository indicadorRepository;
//
//    @InjectMocks
//    private IndicadorBiomedicoService indicadorService;
//
//    private IndicadorBiomedico indicador;
//
//    @BeforeEach
//    void setUp() {
//        indicador = IndicadorBiomedico.builder()
//                .id(1)
//                .idUsuario(1)
//                .data(LocalDate.now())
//                .pesoKg(70.0)
//                .alturaCm(175.0)
//                .percentualGordura(15.0)
//                .percentualMassaMagra(60.0)
//                .imc(22.86)
//                .build();
//    }
//
//    @Test
//    @DisplayName("Deve cadastrar indicador com sucesso")
//    void testCadastrarIndicador_Success() {
//        when(indicadorRepository.salvar(any(IndicadorBiomedico.class))).thenReturn(indicador);
//
//        IndicadorBiomedico result = indicadorService.cadastrarIndicador(1, LocalDate.now(), 70.0, 175.0, 15.0, 60.0);
//
//        assertNotNull(result);
//        assertEquals(70.0, result.getPesoKg());
//        verify(indicadorRepository).salvar(any(IndicadorBiomedico.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para peso ou altura inválidos")
//    void testCadastrarIndicador_PesoAlturaInvalidos() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(1, LocalDate.now(), 0, 175.0, 15.0, 60.0);
//        });
//
//        assertEquals("Peso e altura devem ser maiores que zero.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para percentuais negativos")
//    void testCadastrarIndicador_PercentuaisNegativos() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(1, LocalDate.now(), 70.0, 175.0, -1.0, 60.0);
//        });
//
//        assertEquals("Percentuais de gordura e massa magra não podem ser negativos.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve gerar relatório por data com sucesso")
//    void testGerarRelatorioPorData_Success() {
//        LocalDate inicio = LocalDate.of(2023, 1, 1);
//        LocalDate fim = LocalDate.of(2023, 12, 31);
//        List<IndicadorBiomedico> indicadores = List.of(indicador);
//        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim)).thenReturn(indicadores);
//
//        List<IndicadorBiomedico> result = indicadorService.gerarRelatorioPorData(1, inicio, fim);
//
//        assertEquals(1, result.size());
//        assertEquals(indicadores, result);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para datas nulas")
//    void testGerarRelatorioPorData_DatasNulas() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.gerarRelatorioPorData(1, null, LocalDate.now());
//        });
//
//        assertEquals("Datas de início e fim não podem ser nulas.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para data início posterior à fim")
//    void testGerarRelatorioPorData_DataInicioPosterior() {
//        LocalDate inicio = LocalDate.of(2023, 12, 31);
//        LocalDate fim = LocalDate.of(2023, 1, 1);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.gerarRelatorioPorData(1, inicio, fim);
//        });
//
//        assertEquals("Data de início não pode ser posterior à data de fim.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve gerar relatório de diferença com sucesso")
//    void testGerarRelatorioDiferenca_Success() {
//        LocalDate inicio = LocalDate.of(2023, 1, 1);
//        LocalDate fim = LocalDate.of(2023, 12, 31);
//        List<IndicadorBiomedico> indicadores = List.of(indicador);
//        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim)).thenReturn(indicadores);
//
//        RelatorioDiferencaIndicadores result = indicadorService.gerarRelatorioDiferenca(1, inicio, fim);
//
//        assertNotNull(result);
//        assertEquals(inicio, result.getDataInicio());
//        assertEquals(fim, result.getDataFim());
//    }
//
//    @Test
//    @DisplayName("Deve listar todos os indicadores do usuário")
//    void testListarTodosDoUsuario() {
//        List<IndicadorBiomedico> indicadores = List.of(indicador);
//        when(indicadorRepository.listarPorUsuario(1)).thenReturn(indicadores);
//
//        List<IndicadorBiomedico> result = indicadorService.listarTodosDoUsuario(1);
//
//        assertEquals(1, result.size());
//        assertEquals(indicadores, result);
//    }
//
//    // Note: importarIndicadoresCsv and exportarRelatorioPorDataParaCsv involve file I/O, which is hard to test without mocking files.
//    // For coverage, we can skip or mock if possible, but since it's complex, focus on other methods.
//
//    @Test
//    @DisplayName("Deve lançar exceção para data fim anterior à data início")
//    void testGerarRelatorioPorData_DataFimAnterior() {
//        LocalDate inicio = LocalDate.of(2023, 12, 31);
//        LocalDate fim = LocalDate.of(2023, 1, 1);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.gerarRelatorioPorData(1, inicio, fim);
//        });
//
//        assertEquals("Data de início não pode ser posterior à data de fim.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve cadastrar indicador com percentuais zero")
//    void testCadastrarIndicador_PercentuaisZero() {
//        when(indicadorRepository.salvar(any(IndicadorBiomedico.class))).thenReturn(indicador);
//
//        IndicadorBiomedico result = indicadorService.cadastrarIndicador(1, LocalDate.now(), 70.0, 175.0, 0.0, 0.0);
//
//        assertNotNull(result);
//        verify(indicadorRepository).salvar(any(IndicadorBiomedico.class));
//    }
//
//    @Test
//    @DisplayName("Deve gerar relatório com lista vazia")
//    void testGerarRelatorioDiferenca_ListaVazia() {
//        LocalDate inicio = LocalDate.of(2023, 1, 1);
//        LocalDate fim = LocalDate.of(2023, 12, 31);
//        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim)).thenReturn(List.of());
//
//        RelatorioDiferencaIndicadores result = indicadorService.gerarRelatorioDiferenca(1, inicio, fim);
//
//        assertNotNull(result);
//        assertEquals(inicio, result.getDataInicio());
//        assertEquals(fim, result.getDataFim());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção ao importar CSV de arquivo inexistente")
//    void testImportarIndicadoresCsv_ArquivoInexistente() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.importarIndicadoresCsv(1, "arquivo_inexistente.csv");
//        });
//
//        assertTrue(exception.getMessage().contains("não encontrado"));
//    }
//
//    @Test
//    @DisplayName("Deve cadastrar indicador com data nula usando data atual")
//    void testCadastrarIndicador_DataNula() {
//        when(indicadorRepository.salvar(any(IndicadorBiomedico.class))).thenReturn(indicador);
//
//        IndicadorBiomedico result = indicadorService.cadastrarIndicador(1, null, 70.0, 175.0, 15.0, 60.0);
//
//        assertNotNull(result);
//        verify(indicadorRepository).salvar(any(IndicadorBiomedico.class));
//    }
//
//    @Test
//    @DisplayName("Deve gerar relatório ordenado por data")
//    void testGerarRelatorioPorData_Ordenado() {
//        LocalDate inicio = LocalDate.of(2023, 1, 1);
//        LocalDate fim = LocalDate.of(2023, 12, 31);
//
//        IndicadorBiomedico ind1 = IndicadorBiomedico.builder().data(LocalDate.of(2023, 6, 1)).pesoKg(70.0).build();
//        IndicadorBiomedico ind2 = IndicadorBiomedico.builder().data(LocalDate.of(2023, 1, 15)).pesoKg(72.0).build();
//
//        when(indicadorRepository.buscarPorPeriodo(1, inicio, fim)).thenReturn(List.of(ind1, ind2));
//
//        List<IndicadorBiomedico> result = indicadorService.gerarRelatorioPorData(1, inicio, fim);
//
//        assertEquals(2, result.size());
//        assertTrue(result.get(0).getData().isBefore(result.get(1).getData()));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para altura zero")
//    void testCadastrarIndicador_AlturaZero() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(1, LocalDate.now(), 70.0, 0, 15.0, 60.0);
//        });
//
//        assertEquals("Peso e altura devem ser maiores que zero.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para massa magra negativa")
//    void testCadastrarIndicador_MassaMagraNegativa() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            indicadorService.cadastrarIndicador(1, LocalDate.now(), 70.0, 175.0, 15.0, -1.0);
//        });
//
//        assertEquals("Percentuais de gordura e massa magra não podem ser negativos.", exception.getMessage());
//    }
//}
//
