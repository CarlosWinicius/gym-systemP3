package br.upe;
import br.upe.controller.business.RelatorioDiferencaIndicadores;
import br.upe.data.entities.IndicadorBiomedico;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RelatorioDiferencaIndicadoresTest {

    private RelatorioDiferencaIndicadores relatorio;

    @BeforeEach
    void setup() {
        IndicadorBiomedico inicial = new IndicadorBiomedico();
        IndicadorBiomedico finalObj = new IndicadorBiomedico();


        inicial.setPesoKg(80.0);
        inicial.setPercentualGordura(25.0);
        inicial.setPercentualMassaMagra(75.0);
        inicial.setImc(24.0);

        finalObj.setPesoKg(78.0);
        finalObj.setPercentualGordura(23.0);
        finalObj.setPercentualMassaMagra(77.0);
        finalObj.setImc(23.5);
        relatorio = new RelatorioDiferencaIndicadores();
        relatorio.setDataInicio(LocalDate.of(2024, 1, 1));
        relatorio.setDataFim(LocalDate.of(2024, 1, 31));

        relatorio.setIndicadorInicial(Optional.of(inicial));
        relatorio.setIndicadorFinal(Optional.of(finalObj));

        relatorio.calcularDiferencas();
    }

    @Test
    void testCalcularDiferencasCorretamente() {
        assertEquals(-2.0, relatorio.getDiferencaPeso(), 0.01);
        assertEquals(-2.0, relatorio.getDiferencaPercentualGordura(), 0.01);
        assertEquals(2.0, relatorio.getDiferencaPercentualMassaMagra(), 0.01);
        assertEquals(-0.5, relatorio.getDiferencaImc(), 0.01);
    }

    @Test
    void testToStringConteudoFormatado() {
        String relatorioStr = relatorio.toString();
        assertNotNull(relatorioStr);
        // Assegura que o toString produz algum conteúdo
        assertFalse(relatorioStr.isEmpty());
    }

    @Test
    void testRelatorioSemIndicadores() {
        RelatorioDiferencaIndicadores relatorioVazio = new RelatorioDiferencaIndicadores();
        relatorioVazio.setDataInicio(LocalDate.now());
        relatorioVazio.setDataFim(LocalDate.now().plusDays(7));

        relatorioVazio.calcularDiferencas();

        assertEquals(0.0, relatorioVazio.getDiferencaPeso());
        assertEquals(0.0, relatorioVazio.getDiferencaImc());
    }

    @Test
    void testGettersSetters() {
        RelatorioDiferencaIndicadores r = new RelatorioDiferencaIndicadores();
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 1, 31);

        r.setDataInicio(inicio);
        r.setDataFim(fim);

        assertEquals(inicio, r.getDataInicio());
        assertEquals(fim, r.getDataFim());
    }
}