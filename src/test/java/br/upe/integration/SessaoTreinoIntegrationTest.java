package br.upe.integration;

import br.upe.controller.business.ExercicioService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.controller.business.SessaoTreinoService;
import br.upe.data.beans.*;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.IPlanoTreinoRepository;
import br.upe.data.repository.ISessaoTreinoRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;
import br.upe.data.repository.impl.PlanoTreinoRepositoryImpl;
import br.upe.data.repository.impl.SessaoTreinoRepositoryImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o fluxo completo de sessões de treino
 * Integra SessaoTreinoService + PlanoTreinoService + ExercicioService + repositórios + arquivos CSV
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessaoTreinoIntegrationTest {

    private SessaoTreinoService sessaoTreinoService;
    private PlanoTreinoService planoTreinoService;
    private ExercicioService exercicioService;
    
    private ISessaoTreinoRepository sessaoRepository;
    private IPlanoTreinoRepository planoRepository;
    private IExercicioRepository exercicioRepository;
    
    private static final String TEST_SESSAO_CSV_PATH = "src/test/resources/data/sessoes_integration_test.csv";
    private static final String TEST_PLANO_CSV_PATH = "src/test/resources/data/planos_sessao_integration_test.csv";
    private static final String TEST_EXERCICIO_CSV_PATH = "src/test/resources/data/exercicios_sessao_integration_test.csv";
    private static final int ID_USUARIO_TESTE = 1;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("src/test/resources/data"));
        Files.deleteIfExists(Paths.get(TEST_SESSAO_CSV_PATH));
        Files.deleteIfExists(Paths.get(TEST_PLANO_CSV_PATH));
        Files.deleteIfExists(Paths.get(TEST_EXERCICIO_CSV_PATH));
        
        sessaoRepository = new SessaoTreinoRepositoryImpl(TEST_SESSAO_CSV_PATH);
        planoRepository = new PlanoTreinoRepositoryImpl(TEST_PLANO_CSV_PATH);
        exercicioRepository = new ExercicioRepositoryImpl(TEST_EXERCICIO_CSV_PATH);
        
        sessaoTreinoService = new SessaoTreinoService(sessaoRepository, planoRepository, exercicioRepository);
        planoTreinoService = new PlanoTreinoService(planoRepository, exercicioRepository);
        exercicioService = new ExercicioService(exercicioRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_SESSAO_CSV_PATH));
        Files.deleteIfExists(Paths.get(TEST_PLANO_CSV_PATH));
        Files.deleteIfExists(Paths.get(TEST_EXERCICIO_CSV_PATH));
    }

    @Test
    @Order(1)
    @DisplayName("Integração: Deve iniciar sessão de treino baseada em plano")
    void testIniciarSessao() {
        // Cria plano
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino A");

        // Inicia sessão
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());

        // Verifica
        assertNotNull(sessao);
        assertEquals(ID_USUARIO_TESTE, sessao.getIdUsuario());
        assertEquals(plano.getIdPlano(), sessao.getIdPlanoTreino());
        assertEquals(LocalDate.now(), sessao.getDataSessao());
        assertNotNull(sessao.getItensExecutados());
        assertTrue(sessao.getItensExecutados().isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Não deve iniciar sessão com plano inexistente")
    void testIniciarSessaoPlanoInexistente() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, 99999);
        });
        assertTrue(exception.getMessage().contains("não encontrado ou não pertence a você"));
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Não deve iniciar sessão com plano de outro usuário")
    void testIniciarSessaoPlanoOutroUsuario() {
        // Usuário 1 cria plano
        PlanoTreino planoUser1 = planoTreinoService.criarPlano(1, "Plano User1");

        // Usuário 2 tenta iniciar sessão com plano do usuário 1
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoTreinoService.iniciarSessao(2, planoUser1.getIdPlano());
        });
        assertTrue(exception.getMessage().contains("não encontrado ou não pertence a você"));
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve registrar execuções de exercícios na sessão")
    void testRegistrarExecucoes() {
        // Setup: cria exercícios e plano
        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Supino", "Desc", "/gif/1.gif");
        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Crucifixo", "Desc", "/gif/2.gif");
        
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Peito");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 80, 12);
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 30, 15);

        // Inicia sessão
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());

        // Registra execuções
        sessaoTreinoService.registrarExecucao(sessao, ex1.getIdExercicio(), 12, 80.0);
        sessaoTreinoService.registrarExecucao(sessao, ex2.getIdExercicio(), 15, 30.0);

        // Verifica
        assertEquals(2, sessao.getItensExecutados().size());
        
        ItemSessaoTreino item1 = sessao.getItensExecutados().get(0);
        assertEquals(ex1.getIdExercicio(), item1.getIdExercicio());
        assertEquals(12, item1.getRepeticoesRealizadas());
        assertEquals(80.0, item1.getCargaRealizada());

        ItemSessaoTreino item2 = sessao.getItensExecutados().get(1);
        assertEquals(ex2.getIdExercicio(), item2.getIdExercicio());
        assertEquals(15, item2.getRepeticoesRealizadas());
        assertEquals(30.0, item2.getCargaRealizada());
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve salvar sessão com execuções")
    void testSalvarSessao() {
        // Setup
        Exercicio exercicio = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Agachamento", "Desc", "/gif/ag.gif");
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Pernas");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 100, 10);

        // Inicia e executa sessão
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao, exercicio.getIdExercicio(), 10, 100.0);

        // Salva
        sessaoTreinoService.salvarSessao(sessao);

        // Verifica persistência
        assertNotEquals(0, sessao.getIdSessao());
        Optional<SessaoTreino> sessaoSalva = sessaoRepository.buscarPorId(sessao.getIdSessao());
        assertTrue(sessaoSalva.isPresent());
        assertEquals(1, sessaoSalva.get().getItensExecutados().size());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Não deve salvar sessão vazia")
    void testNaoSalvarSessaoVazia() {
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Vazio");
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());

        // Tenta salvar sem adicionar execuções
        sessaoTreinoService.salvarSessao(sessao);

        // Sessão vazia não deve ser salva (ID permanece 0)
        assertEquals(0, sessao.getIdSessao());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve gerar sugestões quando execução difere do planejado")
    void testGerarSugestoesAlteracao() {
        // Setup: cria exercícios e plano
        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Ex1", "Desc", "/gif/1.gif");
        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Ex2", "Desc", "/gif/2.gif");
        
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Original");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 80, 12);
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 60, 10);

        // Inicia sessão e executa com valores diferentes do planejado
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao, ex1.getIdExercicio(), 15, 85.0); // Aumentou carga e reps
        sessaoTreinoService.registrarExecucao(sessao, ex2.getIdExercicio(), 8, 55.0);  // Diminuiu carga e reps

        // Gera sugestões
        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessao);

        // Verifica sugestões
        assertEquals(2, sugestoes.size());

        // Sugestão para ex1
        SessaoTreinoService.SugestaoAtualizacaoPlano sug1 = sugestoes.stream()
                .filter(s -> s.idExercicio == ex1.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(sug1);
        assertEquals(12, sug1.repPlanejadas);
        assertEquals(15, sug1.repRealizadas);
        assertEquals(80.0, sug1.cargaPlanejada);
        assertEquals(85.0, sug1.cargaRealizada);

        // Sugestão para ex2
        SessaoTreinoService.SugestaoAtualizacaoPlano sug2 = sugestoes.stream()
                .filter(s -> s.idExercicio == ex2.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(sug2);
        assertEquals(10, sug2.repPlanejadas);
        assertEquals(8, sug2.repRealizadas);
        assertEquals(60.0, sug2.cargaPlanejada);
        assertEquals(55.0, sug2.cargaRealizada);
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Não deve gerar sugestões quando execução igual ao planejado")
    void testNaoGerarSugestoesQuandoIgual() {
        // Setup
        Exercicio exercicio = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "ExIgual", "Desc", "/gif/ig.gif");
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Igual");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 70, 12);

        // Executa exatamente como planejado
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao, exercicio.getIdExercicio(), 12, 70.0);

        // Gera sugestões
        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessao);

        // Não deve haver sugestões
        assertTrue(sugestoes.isEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("Integração: Deve aplicar atualizações no plano baseado na sessão")
    void testAplicarAtualizacoesNoPlano() {
        // Setup
        Exercicio exercicio = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "ExAtualizar", "Desc", "/gif/at.gif");
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Atualizar");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 60, 10);

        // Verifica valores originais
        Optional<PlanoTreino> planoOriginal = planoTreinoService.buscarPlanoPorNomeEUsuario(
                ID_USUARIO_TESTE, plano.getNome()
        );
        ItemPlanoTreino itemOriginal = planoOriginal.get().getItensTreino().get(0);
        assertEquals(60, itemOriginal.getCargaKg());
        assertEquals(10, itemOriginal.getRepeticoes());

        // Aplica atualização
        sessaoTreinoService.aplicarAtualizacoesNoPlano(plano.getIdPlano(), exercicio.getIdExercicio(), 12, 65.0);

        // Verifica atualização
        Optional<PlanoTreino> planoAtualizado = planoTreinoService.buscarPlanoPorNomeEUsuario(
                ID_USUARIO_TESTE, plano.getNome()
        );
        assertTrue(planoAtualizado.isPresent());
        ItemPlanoTreino itemAtualizado = planoAtualizado.get().getItensTreino().get(0);
        assertEquals(65, itemAtualizado.getCargaKg());
        assertEquals(12, itemAtualizado.getRepeticoes());

        // Verifica persistência
        Optional<PlanoTreino> planoRecarregado = planoRepository.buscarPorId(plano.getIdPlano());
        assertTrue(planoRecarregado.isPresent());
        assertEquals(65, planoRecarregado.get().getItensTreino().get(0).getCargaKg());
        assertEquals(12, planoRecarregado.get().getItensTreino().get(0).getRepeticoes());
    }

    @Test
    @Order(10)
    @DisplayName("Integração: Fluxo completo - criar plano, executar sessão e atualizar plano")
    void testFluxoCompletoSessaoTreino() {
        // 1. Criar exercícios
        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Supino Reto", "Peito", "/gif/sup.gif");
        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Supino Inclinado", "Peito", "/gif/inc.gif");
        Exercicio ex3 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Crucifixo", "Peito", "/gif/cru.gif");

        // 2. Criar plano de treino
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Peito Completo");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 80, 12);
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 70, 12);
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex3.getIdExercicio(), 30, 15);

        // 3. Iniciar sessão de treino
        SessaoTreino sessao = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        assertNotNull(sessao);

        // 4. Executar exercícios (com valores diferentes do planejado)
        sessaoTreinoService.registrarExecucao(sessao, ex1.getIdExercicio(), 12, 85.0); // Aumentou carga
        sessaoTreinoService.registrarExecucao(sessao, ex2.getIdExercicio(), 10, 70.0); // Diminuiu reps
        sessaoTreinoService.registrarExecucao(sessao, ex3.getIdExercicio(), 15, 32.0); // Aumentou carga

        // 5. Salvar sessão
        sessaoTreinoService.salvarSessao(sessao);
        assertNotEquals(0, sessao.getIdSessao());

        // 6. Verificar sugestões
        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = 
                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessao);
        assertEquals(3, sugestoes.size());

        // 7. Aplicar atualizações no plano
        for (SessaoTreinoService.SugestaoAtualizacaoPlano sugestao : sugestoes) {
            sessaoTreinoService.aplicarAtualizacoesNoPlano(
                    plano.getIdPlano(),
                    sugestao.idExercicio,
                    sugestao.repRealizadas,
                    sugestao.cargaRealizada
            );
        }

        // 8. Verificar que o plano foi atualizado
        Optional<PlanoTreino> planoAtualizado = planoTreinoService.buscarPlanoPorNomeEUsuario(
                ID_USUARIO_TESTE, plano.getNome()
        );
        assertTrue(planoAtualizado.isPresent());
        
        List<ItemPlanoTreino> itens = planoAtualizado.get().getItensTreino();
        assertEquals(3, itens.size());

        // Verifica item 1
        ItemPlanoTreino item1 = itens.stream()
                .filter(i -> i.getIdExercicio() == ex1.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(item1);
        assertEquals(85, item1.getCargaKg());
        assertEquals(12, item1.getRepeticoes());

        // Verifica item 2
        ItemPlanoTreino item2 = itens.stream()
                .filter(i -> i.getIdExercicio() == ex2.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(item2);
        assertEquals(70, item2.getCargaKg());
        assertEquals(10, item2.getRepeticoes());

        // Verifica item 3
        ItemPlanoTreino item3 = itens.stream()
                .filter(i -> i.getIdExercicio() == ex3.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(item3);
        assertEquals(32, item3.getCargaKg());
        assertEquals(15, item3.getRepeticoes());

        // 9. Verificar persistência da sessão
        Optional<SessaoTreino> sessaoSalva = sessaoRepository.buscarPorId(sessao.getIdSessao());
        assertTrue(sessaoSalva.isPresent());
        assertEquals(3, sessaoSalva.get().getItensExecutados().size());

        // 10. Verificar persistência do plano atualizado
        Optional<PlanoTreino> planoRecarregado = planoRepository.buscarPorId(plano.getIdPlano());
        assertTrue(planoRecarregado.isPresent());
        assertEquals(3, planoRecarregado.get().getItensTreino().size());
    }

    @Test
    @Order(11)
    @DisplayName("Integração: Deve permitir múltiplas sessões do mesmo plano")
    void testMultiplasSessoesDoMesmoPlano() {
        // Cria exercício e plano
        Exercicio exercicio = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Agachamento Livre", "Pernas", "/gif/ag.gif");
        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Pernas");
        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 100, 10);

        // Sessão 1
        SessaoTreino sessao1 = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao1, exercicio.getIdExercicio(), 10, 100.0);
        sessaoTreinoService.salvarSessao(sessao1);

        // Sessão 2
        SessaoTreino sessao2 = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao2, exercicio.getIdExercicio(), 10, 105.0);
        sessaoTreinoService.salvarSessao(sessao2);

        // Sessão 3
        SessaoTreino sessao3 = sessaoTreinoService.iniciarSessao(ID_USUARIO_TESTE, plano.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao3, exercicio.getIdExercicio(), 10, 110.0);
        sessaoTreinoService.salvarSessao(sessao3);

        // Verifica que todas foram salvas
        assertNotEquals(0, sessao1.getIdSessao());
        assertNotEquals(0, sessao2.getIdSessao());
        assertNotEquals(0, sessao3.getIdSessao());
        assertNotEquals(sessao1.getIdSessao(), sessao2.getIdSessao());
        assertNotEquals(sessao2.getIdSessao(), sessao3.getIdSessao());

        // Verifica persistência
        assertTrue(sessaoRepository.buscarPorId(sessao1.getIdSessao()).isPresent());
        assertTrue(sessaoRepository.buscarPorId(sessao2.getIdSessao()).isPresent());
        assertTrue(sessaoRepository.buscarPorId(sessao3.getIdSessao()).isPresent());
    }
}

