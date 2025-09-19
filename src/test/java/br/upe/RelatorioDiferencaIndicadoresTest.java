//import br.upe.business.RelatorioDiferencaIndicadores;
//import br.upe.data.beans.IndicadorBiomedico;
//import org.junit.jupiter.api.*;
//import java.io.*;
//import java.nio.file.*;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RelatorioDiferencaIndicadoresTest {
//
//    private RelatorioDiferencaIndicadores relatorio;
//    private IndicadorBiomedico inicial;
//    private IndicadorBiomedico finalObj;
//
//    @BeforeEach
//    public void setup() {
//        relatorio = new RelatorioDiferencaIndicadores();
//
//        // --- CORREÇÃO 1: Declarar as variáveis de data ANTES de usar ---
//        LocalDate dataInicial = LocalDate.of(2025, 1, 1);
//        LocalDate dataFinal = LocalDate.of(2025, 1, 31);
//
//        // Atribuir as datas ao relatório
//        relatorio.dataInicio = dataInicial;
//        relatorio.dataFim = dataFinal;
//
//        // --- CORREÇÃO 2: Usar o construtor correto para IndicadorBiomedico ---
//        // Em vez de new IndicadorBiomedico() e vários setters, passamos os dados diretamente.
//        // Assumindo um construtor como: (id, idUsuario, data, peso, gordura, massaMagra, imc, ...)
//        inicial = new IndicadorBiomedico(
//                1,                  // ID do indicador (exemplo)
//                1,                  // ID do usuário (exemplo)
//                dataInicial,        // Data da medição
//                70.0,               // Peso em Kg
//                20.0,               // Percentual de Gordura
//                75.0,               // Percentual de Massa Magra
//                22.0,               // IMC
//                0.0                 // Outro campo double, se houver (placeholder)
//        );
//
//        finalObj = new IndicadorBiomedico(
//                2,                  // ID do indicador (exemplo)
//                1,                  // ID do usuário (exemplo)
//                dataFinal,          // Data da medição
//                68.0,               // Peso em Kg
//                18.0,               // Percentual de Gordura
//                77.0,               // Percentual de Massa Magra
//                21.5,               // IMC
//                0.0                 // Outro campo double, se houver (placeholder)
//        );
//
//        relatorio.indicadorInicial = Optional.of(inicial);
//        relatorio.indicadorFinal = Optional.of(finalObj);
//
//        relatorio.calcularDiferencas();
//    }
//
//    @Test
//    public void testCalcularDiferencasCorretamente() {
//        assertEquals(-2.0, relatorio.diferencaPeso, 0.01);
//        assertEquals(-2.0, relatorio.diferencaPercentualGordura, 0.01);
//        assertEquals(2.0, relatorio.diferencaPercentualMassaMagra, 0.01);
//        assertEquals(-0.5, relatorio.diferencaImc, 0.01);
//    }
//
//    @Test
//    public void testToStringConteudoFormatado() {
//        String relatorioStr = relatorio.toString();
//        assertTrue(relatorioStr.contains("Relatório de Evolução"));
//        assertTrue(relatorioStr.contains("Peso (kg)"));
//        assertTrue(relatorioStr.contains("Inicial"));
//        assertTrue(relatorioStr.contains("Final"));
//        assertTrue(relatorioStr.contains("-2.0")); // diferença peso negativa
//    }
//
//    @Test
//    public void testExportarParaCsvCriaArquivo() throws IOException {
//        String caminho = "test-relatorio.csv";
//
//        // Garante que o arquivo não exista antes
//        Files.deleteIfExists(Paths.get(caminho));
//
//        relatorio.exportarParaCsv(caminho);
//
//        assertTrue(Files.exists(Paths.get(caminho)));
//
//        // Ler conteúdo e verificar algumas linhas
//        String conteudo = Files.readString(Paths.get(caminho));
//        assertTrue(conteudo.contains("Peso (kg)"));
//        assertTrue(conteudo.contains("-2.0"));
//
//        Files.deleteIfExists(Paths.get(caminho));
//    }
//}