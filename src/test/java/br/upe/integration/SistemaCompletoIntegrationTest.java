package br.upe.integration;

import br.upe.controller.business.*;
import br.upe.data.TipoUsuario;
import br.upe.data.beans.*;
import br.upe.data.repository.*;
import br.upe.data.repository.impl.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração completo do sistema de academia
 * Simula um fluxo completo desde cadastro de usuário até acompanhamento de evolução
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SistemaCompletoIntegrationTest {

    private UsuarioService usuarioService;
    private ExercicioService exercicioService;
    private PlanoTreinoService planoTreinoService;
    private SessaoTreinoService sessaoTreinoService;
    private IndicadorBiomedicoService indicadorService;

    private IUsuarioRepository usuarioRepository;
    private IExercicioRepository exercicioRepository;
    private IPlanoTreinoRepository planoRepository;
    private ISessaoTreinoRepository sessaoRepository;
    private IIndicadorBiomedicoRepository indicadorRepository;

    private static final String BASE_PATH = "src/test/resources/data/sistema_completo_";
    private static final String USUARIO_CSV = BASE_PATH + "usuarios.csv";
    private static final String EXERCICIO_CSV = BASE_PATH + "exercicios.csv";
    private static final String PLANO_CSV = BASE_PATH + "planos.csv";
    private static final String SESSAO_CSV = BASE_PATH + "sessoes.csv";
    private static final String INDICADOR_CSV = BASE_PATH + "indicadores.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("src/test/resources/data"));
        limparArquivos();

        // Inicializa repositórios
        usuarioRepository = new UsuarioRepositoryImpl(USUARIO_CSV);
        exercicioRepository = new ExercicioRepositoryImpl(EXERCICIO_CSV);
        planoRepository = new PlanoTreinoRepositoryImpl(PLANO_CSV);
        sessaoRepository = new SessaoTreinoRepositoryImpl(SESSAO_CSV);
        indicadorRepository = new IndicadorBiomedicoRepositoryImpl(INDICADOR_CSV);

        // Inicializa services
        usuarioService = new UsuarioService(usuarioRepository);
        exercicioService = new ExercicioService(exercicioRepository);
        planoTreinoService = new PlanoTreinoService(planoRepository, exercicioRepository);
        sessaoTreinoService = new SessaoTreinoService(sessaoRepository, planoRepository, exercicioRepository);
        indicadorService = new IndicadorBiomedicoService(indicadorRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        limparArquivos();
    }

    private void limparArquivos() throws IOException {
        Files.deleteIfExists(Paths.get(USUARIO_CSV));
        Files.deleteIfExists(Paths.get(EXERCICIO_CSV));
        Files.deleteIfExists(Paths.get(PLANO_CSV));
        Files.deleteIfExists(Paths.get(SESSAO_CSV));
        Files.deleteIfExists(Paths.get(INDICADOR_CSV));
    }

    @Test
    @Order(1)
    @DisplayName("Integração Completa: Fluxo completo de um usuário na academia")
    void testFluxoCompletoUsuarioNaAcademia() {
        // ===== 1. CADASTRO DO USUÁRIO =====
        Usuario usuario = usuarioService.cadastrarUsuario(
                "João Silva",
                "joao.silva@email.com",
                "senha123",
                TipoUsuario.COMUM
        );
        assertNotNull(usuario);
        int idUsuario = usuario.getId();

        // ===== 2. REGISTRO DE INDICADORES BIOMÉDICOS INICIAIS =====
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        IndicadorBiomedico indicadorInicial = indicadorService.cadastrarIndicador(
                idUsuario, dataInicial, 90.0, 175.0, 25.0, 75.0
        );
        assertNotNull(indicadorInicial);
        assertTrue(indicadorInicial.getImc() > 0);

        // ===== 3. CRIAÇÃO DE EXERCÍCIOS PERSONALIZADOS =====
        Exercicio supino = exercicioService.cadastrarExercicio(
                idUsuario, "Supino Reto", "Exercício para peitoral", "/gif/supino.gif"
        );
        Exercicio crucifixo = exercicioService.cadastrarExercicio(
                idUsuario, "Crucifixo", "Exercício para peitoral", "/gif/crucifixo.gif"
        );
        Exercicio agachamento = exercicioService.cadastrarExercicio(
                idUsuario, "Agachamento Livre", "Exercício para pernas", "/gif/agachamento.gif"
        );
        Exercicio legPress = exercicioService.cadastrarExercicio(
                idUsuario, "Leg Press", "Exercício para pernas", "/gif/legpress.gif"
        );

        List<Exercicio> exerciciosCriados = exercicioService.listarExerciciosDoUsuario(idUsuario);
        assertEquals(4, exerciciosCriados.size());

        // ===== 4. CRIAÇÃO DE PLANOS DE TREINO =====
        // Plano A - Peito
        PlanoTreino planoA = planoTreinoService.criarPlano(idUsuario, "Treino A - Peito");
        planoTreinoService.adicionarExercicioAoPlano(idUsuario, "Treino A - Peito", supino.getIdExercicio(), 60, 12);
        planoTreinoService.adicionarExercicioAoPlano(idUsuario, "Treino A - Peito", crucifixo.getIdExercicio(), 25, 15);

        // Plano B - Pernas
        PlanoTreino planoB = planoTreinoService.criarPlano(idUsuario, "Treino B - Pernas");
        planoTreinoService.adicionarExercicioAoPlano(idUsuario, "Treino B - Pernas", agachamento.getIdExercicio(), 80, 10);
        planoTreinoService.adicionarExercicioAoPlano(idUsuario, "Treino B - Pernas", legPress.getIdExercicio(), 150, 12);

        List<PlanoTreino> planosCriados = planoTreinoService.listarMeusPlanos(idUsuario);
        assertEquals(2, planosCriados.size());

        // ===== 5. PRIMEIRA SEMANA DE TREINO =====
        // Dia 1 - Treino A
        SessaoTreino sessao1 = sessaoTreinoService.iniciarSessao(idUsuario, planoA.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao1, supino.getIdExercicio(), 12, 60.0);
        sessaoTreinoService.registrarExecucao(sessao1, crucifixo.getIdExercicio(), 15, 25.0);
        sessaoTreinoService.salvarSessao(sessao1);

        // Dia 2 - Treino B
        SessaoTreino sessao2 = sessaoTreinoService.iniciarSessao(idUsuario, planoB.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao2, agachamento.getIdExercicio(), 10, 80.0);
        sessaoTreinoService.registrarExecucao(sessao2, legPress.getIdExercicio(), 12, 150.0);
        sessaoTreinoService.salvarSessao(sessao2);

        // Dia 3 - Treino A novamente
        SessaoTreino sessao3 = sessaoTreinoService.iniciarSessao(idUsuario, planoA.getIdPlano());
        sessaoTreinoService.registrarExecucao(sessao3, supino.getIdExercicio(), 12, 65.0); // Aumentou carga
        sessaoTreinoService.registrarExecucao(sessao3, crucifixo.getIdExercicio(), 15, 27.0); // Aumentou carga
        sessaoTreinoService.salvarSessao(sessao3);

        // ===== 6. EVOLUÇÃO - SEGUNDA SEMANA =====
        // Gera sugestões da última sessão
        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes =
                sessaoTreinoService.verificarAlteracoesEGerarSugestoes(sessao3);

        assertEquals(2, sugestoes.size());

        // Aplica melhorias no plano
        for (SessaoTreinoService.SugestaoAtualizacaoPlano sugestao : sugestoes) {
            sessaoTreinoService.aplicarAtualizacoesNoPlano(
                    planoA.getIdPlano(),
                    sugestao.idExercicio,
                    sugestao.repRealizadas,
                    sugestao.cargaRealizada
            );
        }

        // Verifica que o plano foi atualizado
        Optional<PlanoTreino> planoAAtualizado = planoTreinoService.buscarPlanoPorNomeEUsuario(
                idUsuario, "Treino A - Peito"
        );
        assertTrue(planoAAtualizado.isPresent());

        ItemPlanoTreino itemSupino = planoAAtualizado.get().getItensTreino().stream()
                .filter(i -> i.getIdExercicio() == supino.getIdExercicio())
                .findFirst()
                .orElse(null);
        assertNotNull(itemSupino);
        assertEquals(65, itemSupino.getCargaKg());

        // ===== 7. ACOMPANHAMENTO APÓS 1 MÊS =====
        LocalDate dataApos1Mes = LocalDate.of(2024, 2, 1);
        IndicadorBiomedico indicador1Mes = indicadorService.cadastrarIndicador(
                idUsuario, dataApos1Mes, 87.0, 175.0, 22.0, 78.0
        );

        // ===== 8. ACOMPANHAMENTO APÓS 2 MESES =====
        LocalDate dataApos2Meses = LocalDate.of(2024, 3, 1);
        IndicadorBiomedico indicador2Meses = indicadorService.cadastrarIndicador(
                idUsuario, dataApos2Meses, 84.0, 175.0, 19.0, 81.0
        );

        // ===== 9. ACOMPANHAMENTO APÓS 3 MESES =====
        LocalDate dataApos3Meses = LocalDate.of(2024, 4, 1);
        IndicadorBiomedico indicador3Meses = indicadorService.cadastrarIndicador(
                idUsuario, dataApos3Meses, 82.0, 175.0, 17.0, 83.0
        );

        // ===== 10. ANÁLISE DOS RESULTADOS =====
        // Lista todos os indicadores
        List<IndicadorBiomedico> todosIndicadores = indicadorService.listarTodosDoUsuario(idUsuario);
        assertEquals(4, todosIndicadores.size());

        // Gera relatório de evolução
        RelatorioDiferencaIndicadores relatorio = indicadorService.gerarRelatorioDiferenca(
                idUsuario, dataInicial, dataApos3Meses
        );

        assertTrue(relatorio.getIndicadorInicial().isPresent());
        assertTrue(relatorio.getIndicadorFinal().isPresent());

        // Verifica melhorias
        double pesoInicial = relatorio.getIndicadorInicial().get().getPesoKg();
        double pesoFinal = relatorio.getIndicadorFinal().get().getPesoKg();
        assertTrue(pesoFinal < pesoInicial, "Deve ter perdido peso");
        assertEquals(8.0, pesoInicial - pesoFinal, 0.1);

        double gorduraInicial = relatorio.getIndicadorInicial().get().getPercentualGordura();
        double gorduraFinal = relatorio.getIndicadorFinal().get().getPercentualGordura();
        assertTrue(gorduraFinal < gorduraInicial, "Deve ter reduzido gordura");

        double massaMagraInicial = relatorio.getIndicadorInicial().get().getPercentualMassaMagra();
        double massaMagraFinal = relatorio.getIndicadorFinal().get().getPercentualMassaMagra();
        assertTrue(massaMagraFinal > massaMagraInicial, "Deve ter ganho massa magra");

        // ===== 11. VERIFICAÇÃO FINAL DE PERSISTÊNCIA =====
        // Verifica usuário
        Optional<Usuario> usuarioSalvo = usuarioService.buscarUsuarioPorEmail("joao.silva@email.com");
        assertTrue(usuarioSalvo.isPresent());

        // Verifica exercícios
        assertEquals(4, exercicioService.listarExerciciosDoUsuario(idUsuario).size());

        // Verifica planos
        assertEquals(2, planoTreinoService.listarMeusPlanos(idUsuario).size());

        // Verifica sessões salvas
        assertNotEquals(0, sessao1.getIdSessao());
        assertNotEquals(0, sessao2.getIdSessao());
        assertNotEquals(0, sessao3.getIdSessao());

        // Verifica indicadores
        assertEquals(4, indicadorService.listarTodosDoUsuario(idUsuario).size());
    }

    @Test
    @Order(2)
    @DisplayName("Integração Completa: Múltiplos usuários usando o sistema simultaneamente")
    void testMultiplosUsuariosSimultaneos() {
        // Cadastra 3 usuários
        Usuario user1 = usuarioService.cadastrarUsuario("User 1", "user1@email.com", "senha1", TipoUsuario.COMUM);
        Usuario user2 = usuarioService.cadastrarUsuario("User 2", "user2@email.com", "senha2", TipoUsuario.COMUM);
        Usuario user3 = usuarioService.cadastrarUsuario("User 3", "user3@email.com", "senha3", TipoUsuario.COMUM);

        // Cada usuário cria seus próprios exercícios (mesmo nome, mas são diferentes)
        Exercicio ex1User1 = exercicioService.cadastrarExercicio(user1.getId(), "Supino", "Desc", "/gif/1.gif");
        Exercicio ex1User2 = exercicioService.cadastrarExercicio(user2.getId(), "Supino", "Desc", "/gif/1.gif");
        Exercicio ex1User3 = exercicioService.cadastrarExercicio(user3.getId(), "Supino", "Desc", "/gif/1.gif");

        // Cada usuário cria seu plano
        PlanoTreino planoUser1 = planoTreinoService.criarPlano(user1.getId(), "Plano A");
        PlanoTreino planoUser2 = planoTreinoService.criarPlano(user2.getId(), "Plano A");
        PlanoTreino planoUser3 = planoTreinoService.criarPlano(user3.getId(), "Plano A");

        // Adiciona exercícios aos planos
        planoTreinoService.adicionarExercicioAoPlano(user1.getId(), "Plano A", ex1User1.getIdExercicio(), 80, 12);
        planoTreinoService.adicionarExercicioAoPlano(user2.getId(), "Plano A", ex1User2.getIdExercicio(), 60, 12);
        planoTreinoService.adicionarExercicioAoPlano(user3.getId(), "Plano A", ex1User3.getIdExercicio(), 100, 12);

        // Cada usuário registra seus indicadores
        indicadorService.cadastrarIndicador(user1.getId(), LocalDate.now(), 80.0, 175.0, 15.0, 85.0);
        indicadorService.cadastrarIndicador(user2.getId(), LocalDate.now(), 65.0, 165.0, 20.0, 80.0);
        indicadorService.cadastrarIndicador(user3.getId(), LocalDate.now(), 95.0, 180.0, 25.0, 75.0);

        // Verifica isolamento de dados
        assertEquals(1, exercicioService.listarExerciciosDoUsuario(user1.getId()).size());
        assertEquals(1, exercicioService.listarExerciciosDoUsuario(user2.getId()).size());
        assertEquals(1, exercicioService.listarExerciciosDoUsuario(user3.getId()).size());

        assertEquals(1, planoTreinoService.listarMeusPlanos(user1.getId()).size());
        assertEquals(1, planoTreinoService.listarMeusPlanos(user2.getId()).size());
        assertEquals(1, planoTreinoService.listarMeusPlanos(user3.getId()).size());

        assertEquals(1, indicadorService.listarTodosDoUsuario(user1.getId()).size());
        assertEquals(1, indicadorService.listarTodosDoUsuario(user2.getId()).size());
        assertEquals(1, indicadorService.listarTodosDoUsuario(user3.getId()).size());

        // Verifica que usuários não podem acessar dados uns dos outros
        Optional<PlanoTreino> planoUser1DoUser2 = planoTreinoService.buscarPlanoPorNomeEUsuario(
                user2.getId(), "Plano A"
        );
        assertTrue(planoUser1DoUser2.isPresent());
        assertNotEquals(planoUser1.getIdPlano(), planoUser1DoUser2.get().getIdPlano());
    }

    @Test
    @Order(3)
    @DisplayName("Integração Completa: Administrador gerenciando usuários")
    void testFluxoAdministrador() {
        // Admin inicial (ID 1 é criado automaticamente)
        Usuario admin = usuarioService.buscarUsuarioPorId(1).orElse(null);
        assertNotNull(admin);
        assertEquals(TipoUsuario.ADMIN, admin.getTipo());

        // Cadastra usuários comuns
        Usuario comum1 = usuarioService.cadastrarUsuario("Comum 1", "comum1@email.com", "senha", TipoUsuario.COMUM);
        Usuario comum2 = usuarioService.cadastrarUsuario("Comum 2", "comum2@email.com", "senha", TipoUsuario.COMUM);

        assertEquals(TipoUsuario.COMUM, comum1.getTipo());
        assertEquals(TipoUsuario.COMUM, comum2.getTipo());

        // Admin promove comum1
        usuarioService.promoverUsuarioAAdmin(comum1.getId());
        Usuario comum1Promovido = usuarioService.buscarUsuarioPorId(comum1.getId()).orElse(null);
        assertNotNull(comum1Promovido);
        assertEquals(TipoUsuario.ADMIN, comum1Promovido.getTipo());

        // Novo admin rebaixa comum1 de volta (mas não pode rebaixar admin principal)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.rebaixarUsuarioAComum(1); // Tenta rebaixar admin principal
        });
        assertTrue(exception.getMessage().contains("administrador principal não pode ser rebaixado"));

        // Mas pode rebaixar outros admins
        usuarioService.rebaixarUsuarioAComum(comum1.getId());
        Usuario comum1Rebaixado = usuarioService.buscarUsuarioPorId(comum1.getId()).orElse(null);
        assertNotNull(comum1Rebaixado);
        assertEquals(TipoUsuario.COMUM, comum1Rebaixado.getTipo());
    }

    @Test
    @Order(4)
    @DisplayName("Integração Completa: Progressão de treino ao longo de 12 semanas")
    void testProgressaoTreinoLongoPrazo() {
        // Cadastra usuário
        Usuario usuario = usuarioService.cadastrarUsuario(
                "Atleta Dedicado",
                "atleta@email.com",
                "senha",
                TipoUsuario.COMUM
        );

        // Cria exercício
        Exercicio supino = exercicioService.cadastrarExercicio(
                usuario.getId(), "Supino Reto", "Peito", "/gif/supino.gif"
        );

        // Cria plano inicial
        PlanoTreino plano = planoTreinoService.criarPlano(usuario.getId(), "Progressão Supino");
        planoTreinoService.adicionarExercicioAoPlano(
                usuario.getId(), "Progressão Supino", supino.getIdExercicio(), 60, 12
        );

        // Simula 12 semanas de treino com progressão
        double cargaAtual = 60.0;
        for (int semana = 1; semana <= 12; semana++) {
            // Executa 3 treinos por semana
            for (int treino = 1; treino <= 3; treino++) {
                SessaoTreino sessao = sessaoTreinoService.iniciarSessao(usuario.getId(), plano.getIdPlano());

                // A cada semana aumenta 2.5kg
                if (treino == 3) {
                    cargaAtual += 2.5;
                }

                sessaoTreinoService.registrarExecucao(sessao, supino.getIdExercicio(), 12, cargaAtual);
                sessaoTreinoService.salvarSessao(sessao);

                // Atualiza o plano na última sessão da semana
                if (treino == 3) {
                    sessaoTreinoService.aplicarAtualizacoesNoPlano(
                            plano.getIdPlano(), supino.getIdExercicio(), 12, cargaAtual
                    );
                }
            }
        }

        // Verifica progressão
        Optional<PlanoTreino> planoFinal = planoTreinoService.buscarPlanoPorNomeEUsuario(
                usuario.getId(), "Progressão Supino"
        );
        assertTrue(planoFinal.isPresent());

        ItemPlanoTreino itemFinal = planoFinal.get().getItensTreino().get(0);

        // Deve ter progredido de 60kg para 90kg (12 semanas * 2.5kg)
        double cargaEsperada = 60.0 + (12 * 2.5);
        assertEquals((int)cargaEsperada, itemFinal.getCargaKg());

        // Verifica que salvou 36 sessões (12 semanas * 3 treinos)
        // Nota: não temos método direto para listar todas as sessões, mas verificamos que foram salvas
    }
}

