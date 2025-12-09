//package br.upe;
//
//import br.upe.controller.business.RelatorioDiferencaIndicadores;
//import br.upe.data.beans.IndicadorBiomedico;
//import org.junit.jupiter.api.*;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RelatorioDiferencaIndicadoresTest {
//
//    private RelatorioDiferencaIndicadores relatorio;
//    private IndicadorBiomedico inicial;
//    private IndicadorBiomedico finalObj;
//
//    @BeforeEach
//    void setup() {
//        relatorio = new RelatorioDiferencaIndicadores();
//
//        relatorio.setDataInicio(LocalDate.of(2025, 1, 1));
//        relatorio.setDataFim(LocalDate.of(2025, 1, 31));
//
//        // Criar Indicadores de exemplo
//        inicial = IndicadorBiomedico.builder()
//                .pesoKg(70.0)
//                .percentualGordura(20.0)
//                .percentualMassaMagra(75.0)
//                .imc(22.0)
//                .build();
//
//        finalObj = IndicadorBiomedico.builder()
//                .pesoKg(68.0)
//                .percentualGordura(18.0)
//                .percentualMassaMagra(77.0)
//                .imc(21.5)
//                .build();
//
//        relatorio.setIndicadorInicial(Optional.of(inicial));
//        relatorio.setIndicadorFinal(Optional.of(finalObj));
//
//        relatorio.calcularDiferencas();
//    }
//
//    @Test
//    void testCalcularDiferencasCorretamente() {
//        assertEquals(-2.0, relatorio.getDiferencaPeso(), 0.01);
//        assertEquals(-2.0, relatorio.getDiferencaPercentualGordura(), 0.01);
//        assertEquals(2.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
//        assertEquals(-0.5, relatorio.getDiferencaImc(), 0.01);
//    }
//
//    @Test
//    void testToStringConteudoFormatado() {
//        String relatorioStr = relatorio.toString();
//        assertNotNull(relatorioStr);
//        assertTrue(relatorioStr.contains("Relatório") || !relatorioStr.isEmpty());
//    }
//
//    @Test
//    void testRelatorioSemIndicadores() {
//        RelatorioDiferencaIndicadores relatorioVazio = new RelatorioDiferencaIndicadores();
//        relatorioVazio.setDataInicio(LocalDate.now());
//        relatorioVazio.setDataFim(LocalDate.now().plusDays(7));
//
//        relatorioVazio.calcularDiferencas();
//
//        assertEquals(0.0, relatorioVazio.getDiferencaPeso());
//        assertEquals(0.0, relatorioVazio.getDiferencaImc());
//    }
//
//    @Test
//    void testGettersSetters() {
//        RelatorioDiferencaIndicadores r = new RelatorioDiferencaIndicadores();
//        LocalDate inicio = LocalDate.of(2025, 1, 1);
//        LocalDate fim = LocalDate.of(2025, 1, 31);
//
//        r.setDataInicio(inicio);
//        r.setDataFim(fim);
//
//        assertEquals(inicio, r.getDataInicio());
//        assertEquals(fim, r.getDataFim());
//    }
//}
//
//
