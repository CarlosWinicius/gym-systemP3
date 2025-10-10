import br.upe.controller.business.RelatorioDiferencaIndicadores;
import br.upe.data.beans.IndicadorBiomedico;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RelatorioDiferencaIndicadoresTest {

    private RelatorioDiferencaIndicadores relatorio;
    private IndicadorBiomedico inicial;
    private IndicadorBiomedico finalObj;

    @BeforeEach
    public void setup() {
        relatorio = new RelatorioDiferencaIndicadores();

        relatorio.dataInicio = LocalDate.of(2025, 1, 1);
        relatorio.dataFim = LocalDate.of(2025, 1, 31);

        // Criar Indicadores de exemplo
        inicial = new IndicadorBiomedico();
        inicial.setPesoKg(70.0);
        inicial.setPercentualGordura(20.0);
        inicial.setPercentualMassaMagra(75.0);
        inicial.setImc(22.0);

        finalObj = new IndicadorBiomedico();
        finalObj.setPesoKg(68.0);
        finalObj.setPercentualGordura(18.0);
        finalObj.setPercentualMassaMagra(77.0);
        finalObj.setImc(21.5);

        relatorio.indicadorInicial = Optional.of(inicial);
        relatorio.indicadorFinal = Optional.of(finalObj);

        relatorio.calcularDiferencas();
    }

    @Test
    public void testCalcularDiferencasCorretamente() {
        assertEquals(-2.0, relatorio.diferencaPeso, 0.01);
        assertEquals(-2.0, relatorio.diferencaPercentualGordura, 0.01);
        assertEquals(2.0, relatorio.diferencaPercentualMassaMagra, 0.01);
        assertEquals(-0.5, relatorio.diferencaImc, 0.01);
    }

    @Test
    public void testToStringConteudoFormatado() {
        String relatorioStr = relatorio.toString();
        assertTrue(relatorioStr.contains("Relatório de Evolução"));
        assertTrue(relatorioStr.contains("Peso (kg)"));
        assertTrue(relatorioStr.contains("Inicial"));
        assertTrue(relatorioStr.contains("Final"));
        assertTrue(relatorioStr.contains("-2.0")); // diferença peso negativa
    }

    @Test
    public void testExportarParaCsvCriaArquivo() throws IOException {
        String caminho = "test-relatorio.csv";

        // Garante que o arquivo não exista antes
        Files.deleteIfExists(Paths.get(caminho));

        relatorio.exportarParaCsv(caminho);

        assertTrue(Files.exists(Paths.get(caminho)));

        // Ler conteúdo e verificar algumas linhas
        String conteudo = Files.readString(Paths.get(caminho));
        assertTrue(conteudo.contains("Peso (kg)"));
        assertTrue(conteudo.contains("-2.0"));

        Files.deleteIfExists(Paths.get(caminho));
    }

}
