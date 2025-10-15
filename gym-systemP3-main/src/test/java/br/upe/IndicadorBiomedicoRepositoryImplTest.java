//package br.upe;
//
//import br.upe.data.beans.IndicadorBiomedico;
//import br.upe.data.repository.IIndicadorBiomedicoRepository;
//import br.upe.data.repository.impl.IndicadorBiomedicoRepositoryImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class IndicadorBiomedicoRepositoryImplTest {
//
//    private IIndicadorBiomedicoRepository repository;
//    private static final String TEST_CSV_PATH = "src/test/resources/data/indicadores_test.csv";
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Garante que o diretório de teste exista
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        // Apaga o arquivo de teste antes de cada execução para garantir um ambiente limpo
//        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
//
//        // Forçar o repositório a usar o nosso arquivo de teste
//        // Isso requer uma pequena refatoração no repositório para aceitar o caminho do arquivo no construtor
//        // Por enquanto, vamos assumir que ele usa o caminho padrão e vamos lidar com o arquivo nesse caminho
//        repository = new IndicadorBiomedicoRepositoryImpl(); // Este usará o caminho de produção, o que não é ideal
//
//        // A melhor abordagem seria ter um construtor como: new IndicadorBiomedicoRepositoryImpl(TEST_CSV_PATH)
//        // Vamos adaptar o teste para lidar com o arquivo de produção por enquanto, mas isso não é uma boa prática.
//        // O ideal é refatorar o repositório para permitir injeção do caminho do arquivo.
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        // Limpa o arquivo de produção após os testes para não deixar lixo
//        Files.deleteIfExists(Paths.get("src/main/resources/data/indicadores.csv"));
//    }
//
//    private IndicadorBiomedico criarIndicador(int idUsuario, LocalDate data) {
//        return new IndicadorBiomedico(0, idUsuario, data, 80.0, 175.0, 20.0, 70.0, 26.1);
//    }
//
//    @Test
//    void testSalvarEBuscarPorId() {
//        IndicadorBiomedico indicador = criarIndicador(1, LocalDate.now());
//        IndicadorBiomedico salvo = repository.salvar(indicador);
//
//        assertNotEquals(0, salvo.getId());
//
//        Optional<IndicadorBiomedico> buscado = repository.buscarPorId(salvo.getId());
//        assertTrue(buscado.isPresent());
//        assertEquals(salvo.getId(), buscado.get().getId());
//        assertEquals(1, buscado.get().getIdUsuario());
//    }
//
//    @Test
//    void testListarPorUsuario() {
//        repository.salvar(criarIndicador(1, LocalDate.now().minusDays(1)));
//        repository.salvar(criarIndicador(1, LocalDate.now()));
//        repository.salvar(criarIndicador(2, LocalDate.now())); // Outro usuário
//
//        List<IndicadorBiomedico> indicadoresUsuario1 = repository.listarPorUsuario(1);
//        assertEquals(2, indicadoresUsuario1.size());
//
//        List<IndicadorBiomedico> indicadoresUsuario2 = repository.listarPorUsuario(2);
//        assertEquals(1, indicadoresUsuario2.size());
//
//        List<IndicadorBiomedico> indicadoresUsuario3 = repository.listarPorUsuario(3);
//        assertTrue(indicadoresUsuario3.isEmpty());
//    }
//
//    @Test
//    void testBuscarPorPeriodo() {
//        LocalDate hoje = LocalDate.now();
//        repository.salvar(criarIndicador(1, hoje.minusDays(10)));
//        repository.salvar(criarIndicador(1, hoje.minusDays(5)));
//        repository.salvar(criarIndicador(1, hoje));
//        repository.salvar(criarIndicador(1, hoje.plusDays(5)));
//
//        List<IndicadorBiomedico> resultado = repository.buscarPorPeriodo(1, hoje.minusDays(6), hoje.plusDays(1));
//        assertEquals(2, resultado.size());
//        assertTrue(resultado.stream().anyMatch(i -> i.getData().equals(hoje.minusDays(5))));
//        assertTrue(resultado.stream().anyMatch(i -> i.getData().equals(hoje)));
//    }
//
//    @Test
//    void testDeletar() {
//        IndicadorBiomedico salvo = repository.salvar(criarIndicador(1, LocalDate.now()));
//        int idSalvo = salvo.getId();
//
//        Optional<IndicadorBiomedico> antesDeDeletar = repository.buscarPorId(idSalvo);
//        assertTrue(antesDeDeletar.isPresent());
//
//        repository.deletar(idSalvo);
//
//        Optional<IndicadorBiomedico> depoisDeDeletar = repository.buscarPorId(idSalvo);
//        assertFalse(depoisDeDeletar.isPresent());
//    }
//
//    @Test
//    void testCarregamentoCsv() {
//        // Salva alguns dados
//        repository.salvar(criarIndicador(1, LocalDate.now()));
//        repository.salvar(criarIndicador(2, LocalDate.now()));
//
//        // Cria uma nova instância do repositório para forçar o carregamento do CSV
//        IIndicadorBiomedicoRepository novoRepositorio = new IndicadorBiomedicoRepositoryImpl();
//        List<IndicadorBiomedico> todos = novoRepositorio.listarTodos();
//
//        assertFalse(todos.isEmpty());
//        assertEquals(2, todos.size());
//    }
//}
