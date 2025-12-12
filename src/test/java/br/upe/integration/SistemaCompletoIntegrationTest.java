package br.upe.integration;

import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.entities.*;
import br.upe.test.dao.*;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração completo do sistema de academia
 * Simula um fluxo completo desde cadastro de usuário até acompanhamento de evolução
 * Usando banco H2 em memória
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SistemaCompletoIntegrationTest {

    private TestUsuarioDAO usuarioDAO;
    private TestExercicioDAO exercicioDAO;
    private TestPlanoTreinoDAO planoTreinoDAO;
    private TestSessaoTreinoDAO sessaoTreinoDAO;
    private TestIndicadorBiomedicoDAO indicadorDAO;
    private TestItemPlanoTreinoDAO itemPlanoTreinoDAO;
    private TestItemSessaoTreinoDAO itemSessaoTreinoDAO;
    private UsuarioService usuarioService;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = TestConnectionFactory.getTestEntityManager();
        TestConnectionFactory.clearDatabase(em);

        usuarioDAO = new TestUsuarioDAO();
        exercicioDAO = new TestExercicioDAO();
        planoTreinoDAO = new TestPlanoTreinoDAO();
        sessaoTreinoDAO = new TestSessaoTreinoDAO();
        indicadorDAO = new TestIndicadorBiomedicoDAO();
        itemPlanoTreinoDAO = new TestItemPlanoTreinoDAO();
        itemSessaoTreinoDAO = new TestItemSessaoTreinoDAO();
        usuarioService = new UsuarioService(usuarioDAO);
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @AfterAll
    static void tearDownAll() {
        TestConnectionFactory.closeFactory();
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
        Integer idUsuario = usuario.getId();

        // ===== 2. REGISTRO DE INDICADORES BIOMÉDICOS INICIAIS =====
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        IndicadorBiomedico indicadorInicial = cadastrarIndicador(usuario, dataInicial, 90.0, 175.0, 25.0, 75.0);
        assertNotNull(indicadorInicial);
        assertTrue(indicadorInicial.getImc() > 0);

        // ===== 3. CRIAÇÃO DE EXERCÍCIOS PERSONALIZADOS =====
        Exercicio supino = criarExercicio(usuario, "Supino Reto", "Exercício para peitoral", "/gif/supino.gif");
        Exercicio crucifixo = criarExercicio(usuario, "Crucifixo", "Exercício para peitoral", "/gif/crucifixo.gif");
        Exercicio agachamento = criarExercicio(usuario, "Agachamento Livre", "Exercício para pernas", "/gif/agachamento.gif");
        Exercicio legPress = criarExercicio(usuario, "Leg Press", "Exercício para pernas", "/gif/legpress.gif");

        List<Exercicio> exerciciosCriados = exercicioDAO.buscarTodosDoUsuario(idUsuario);
        assertEquals(4, exerciciosCriados.size());

        // ===== 4. CRIAÇÃO DE PLANOS DE TREINO =====
        // Plano A - Peito
        PlanoTreino planoA = criarPlano(usuario, "Treino A - Peito");
        adicionarItemAoPlano(planoA, supino, 60, 12);
        adicionarItemAoPlano(planoA, crucifixo, 25, 15);

        // Plano B - Pernas
        PlanoTreino planoB = criarPlano(usuario, "Treino B - Pernas");
        adicionarItemAoPlano(planoB, agachamento, 80, 10);
        adicionarItemAoPlano(planoB, legPress, 150, 12);

        List<PlanoTreino> planosCriados = planoTreinoDAO.buscarTodosDoUsuario(idUsuario);
        assertEquals(2, planosCriados.size());

        // ===== 5. PRIMEIRA SEMANA DE TREINO =====
        // Dia 1 - Treino A
        SessaoTreino sessao1 = criarSessao(usuario, planoA);
        registrarExecucao(sessao1, supino, 12, 60.0);
        registrarExecucao(sessao1, crucifixo, 15, 25.0);

        // Dia 2 - Treino B
        SessaoTreino sessao2 = criarSessao(usuario, planoB);
        registrarExecucao(sessao2, agachamento, 10, 80.0);
        registrarExecucao(sessao2, legPress, 12, 150.0);

        // Dia 3 - Treino A novamente (com evolução)
        SessaoTreino sessao3 = criarSessao(usuario, planoA);
        registrarExecucao(sessao3, supino, 12, 65.0);  // Aumentou carga
        registrarExecucao(sessao3, crucifixo, 15, 27.0);  // Aumentou carga

        // ===== 6. ACOMPANHAMENTO APÓS 1 MÊS =====
        LocalDate dataApos1Mes = LocalDate.of(2024, 2, 1);
        cadastrarIndicador(usuario, dataApos1Mes, 87.0, 175.0, 22.0, 78.0);

        // ===== 7. ACOMPANHAMENTO APÓS 2 MESES =====
        LocalDate dataApos2Meses = LocalDate.of(2024, 3, 1);
        cadastrarIndicador(usuario, dataApos2Meses, 84.0, 175.0, 19.0, 81.0);

        // ===== 8. ACOMPANHAMENTO APÓS 3 MESES =====
        LocalDate dataApos3Meses = LocalDate.of(2024, 4, 1);
        cadastrarIndicador(usuario, dataApos3Meses, 82.0, 175.0, 17.0, 83.0);

        // ===== 9. ANÁLISE DOS RESULTADOS =====
        // Lista todos os indicadores
        List<IndicadorBiomedico> todosIndicadores = indicadorDAO.listarPorUsuario(idUsuario);
        assertEquals(4, todosIndicadores.size());

        // Busca indicadores do período
        List<IndicadorBiomedico> indicadoresPeriodo = indicadorDAO.buscarPorPeriodo(
                idUsuario, dataInicial, dataApos3Meses
        );
        assertEquals(4, indicadoresPeriodo.size());

        // Ordena por data
        indicadoresPeriodo.sort((a, b) -> a.getDataRegistro().compareTo(b.getDataRegistro()));

        // Verifica melhorias
        IndicadorBiomedico primeiro = indicadoresPeriodo.get(0);
        IndicadorBiomedico ultimo = indicadoresPeriodo.get(indicadoresPeriodo.size() - 1);

        assertTrue(ultimo.getPesoKg() < primeiro.getPesoKg(), "Deve ter perdido peso");
        assertEquals(8.0, primeiro.getPesoKg() - ultimo.getPesoKg(), 0.1);

        assertTrue(ultimo.getPercentualGordura() < primeiro.getPercentualGordura(), "Deve ter reduzido gordura");
        assertTrue(ultimo.getPercentualMassaMagra() > primeiro.getPercentualMassaMagra(), "Deve ter ganho massa magra");

        // ===== 10. VERIFICAÇÃO FINAL DE PERSISTÊNCIA =====
        // Verifica usuário
        Optional<Usuario> usuarioSalvo = usuarioDAO.buscarPorEmail("joao.silva@email.com");
        assertTrue(usuarioSalvo.isPresent());

        // Verifica exercícios
        assertEquals(4, exercicioDAO.buscarTodosDoUsuario(idUsuario).size());

        // Verifica planos
        assertEquals(2, planoTreinoDAO.buscarTodosDoUsuario(idUsuario).size());

        // Verifica sessões salvas
        List<SessaoTreino> sessoesSalvas = sessaoTreinoDAO.buscarTodosDoUsuario(idUsuario);
        assertEquals(3, sessoesSalvas.size());

        // Verifica indicadores
        assertEquals(4, indicadorDAO.listarPorUsuario(idUsuario).size());
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
        Exercicio ex1User1 = criarExercicio(user1, "Supino", "Desc", "/gif/1.gif");
        Exercicio ex1User2 = criarExercicio(user2, "Supino", "Desc", "/gif/1.gif");
        Exercicio ex1User3 = criarExercicio(user3, "Supino", "Desc", "/gif/1.gif");

        // Cada usuário cria seu plano
        PlanoTreino planoUser1 = criarPlano(user1, "Plano A");
        PlanoTreino planoUser2 = criarPlano(user2, "Plano A");
        PlanoTreino planoUser3 = criarPlano(user3, "Plano A");

        // Adiciona exercícios aos planos
        adicionarItemAoPlano(planoUser1, ex1User1, 80, 12);
        adicionarItemAoPlano(planoUser2, ex1User2, 60, 12);
        adicionarItemAoPlano(planoUser3, ex1User3, 100, 12);

        // Cada usuário registra seus indicadores
        cadastrarIndicador(user1, LocalDate.now(), 80.0, 175.0, 15.0, 85.0);
        cadastrarIndicador(user2, LocalDate.now(), 65.0, 165.0, 20.0, 80.0);
        cadastrarIndicador(user3, LocalDate.now(), 95.0, 180.0, 25.0, 75.0);

        // Verifica isolamento de dados
        assertEquals(1, exercicioDAO.buscarTodosDoUsuario(user1.getId()).size());
        assertEquals(1, exercicioDAO.buscarTodosDoUsuario(user2.getId()).size());
        assertEquals(1, exercicioDAO.buscarTodosDoUsuario(user3.getId()).size());

        assertEquals(1, planoTreinoDAO.buscarTodosDoUsuario(user1.getId()).size());
        assertEquals(1, planoTreinoDAO.buscarTodosDoUsuario(user2.getId()).size());
        assertEquals(1, planoTreinoDAO.buscarTodosDoUsuario(user3.getId()).size());

        assertEquals(1, indicadorDAO.listarPorUsuario(user1.getId()).size());
        assertEquals(1, indicadorDAO.listarPorUsuario(user2.getId()).size());
        assertEquals(1, indicadorDAO.listarPorUsuario(user3.getId()).size());

        // Verifica que planos de usuários diferentes têm IDs diferentes
        Optional<PlanoTreino> planoUser1Buscado = planoTreinoDAO.buscarPorNomeEUsuario(user1.getId(), "Plano A");
        Optional<PlanoTreino> planoUser2Buscado = planoTreinoDAO.buscarPorNomeEUsuario(user2.getId(), "Plano A");
        assertTrue(planoUser1Buscado.isPresent());
        assertTrue(planoUser2Buscado.isPresent());
        assertNotEquals(planoUser1Buscado.get().getId(), planoUser2Buscado.get().getId());
    }

    @Test
    @Order(3)
    @DisplayName("Integração Completa: Administrador gerenciando usuários")
    void testFluxoAdministrador() {
        // Cadastra admin
        Usuario admin = usuarioService.cadastrarUsuario("Admin", "admin@email.com", "adminpass", TipoUsuario.ADMIN);
        assertNotNull(admin);
        assertEquals(TipoUsuario.ADMIN, admin.getTipo());

        // Cadastra usuários comuns
        Usuario comum1 = usuarioService.cadastrarUsuario("Comum 1", "comum1@email.com", "pass1", TipoUsuario.COMUM);
        Usuario comum2 = usuarioService.cadastrarUsuario("Comum 2", "comum2@email.com", "pass2", TipoUsuario.COMUM);

        assertEquals(TipoUsuario.COMUM, comum1.getTipo());
        assertEquals(TipoUsuario.COMUM, comum2.getTipo());

        // Admin promove usuário comum a admin
        usuarioService.promoverUsuarioAAdmin(comum1.getId());
        Optional<Usuario> comum1Promovido = usuarioDAO.buscarPorId(comum1.getId());
        assertTrue(comum1Promovido.isPresent());
        assertEquals(TipoUsuario.ADMIN, comum1Promovido.get().getTipo());

        // Admin rebaixa usuário promovido
        usuarioService.rebaixarUsuarioAComum(comum1.getId());
        Optional<Usuario> comum1Rebaixado = usuarioDAO.buscarPorId(comum1.getId());
        assertTrue(comum1Rebaixado.isPresent());
        assertEquals(TipoUsuario.COMUM, comum1Rebaixado.get().getTipo());

        // Lista todos os usuários
        List<Usuario> todosUsuarios = usuarioDAO.listarTodos();
        assertEquals(3, todosUsuarios.size());
    }

    @Test
    @Order(4)
    @DisplayName("Integração Completa: Evolução de treino ao longo do tempo")
    void testEvolucaoTreinoAoLongoDoTempo() {
        Usuario usuario = usuarioService.cadastrarUsuario("Atleta", "atleta@email.com", "senha", TipoUsuario.COMUM);

        // Cria exercício e plano
        Exercicio supino = criarExercicio(usuario, "Supino Reto", "Peito", "/gif/supino.gif");
        PlanoTreino plano = criarPlano(usuario, "Treino Força");
        adicionarItemAoPlano(plano, supino, 60, 10);

        // Simula 8 semanas de treino com evolução progressiva
        double cargaInicial = 60.0;
        for (int semana = 1; semana <= 8; semana++) {
            LocalDate dataSessao = LocalDate.of(2024, 1, 1).plusWeeks(semana - 1);
            SessaoTreino sessao = criarSessaoComData(usuario, plano, dataSessao);

            // Aumenta 5kg a cada 2 semanas
            double cargaAtual = cargaInicial + ((semana - 1) / 2) * 5.0;
            registrarExecucao(sessao, supino, 10, cargaAtual);
        }

        // Verifica evolução
        List<SessaoTreino> todasSessoes = sessaoTreinoDAO.buscarTodosDoUsuario(usuario.getId());
        assertEquals(8, todasSessoes.size());

        // Busca primeira e última sessão
        todasSessoes.sort((a, b) -> a.getDataSessao().compareTo(b.getDataSessao()));
        SessaoTreino primeiraSessao = todasSessoes.get(0);
        SessaoTreino ultimaSessao = todasSessoes.get(todasSessoes.size() - 1);

        List<ItemSessaoTreino> itensPrimeira = itemSessaoTreinoDAO.listarPorSessao(primeiraSessao.getId());
        List<ItemSessaoTreino> itensUltima = itemSessaoTreinoDAO.listarPorSessao(ultimaSessao.getId());

        double cargaPrimeira = itensPrimeira.get(0).getCargaRealizada();
        double cargaUltima = itensUltima.get(0).getCargaRealizada();

        assertTrue(cargaUltima > cargaPrimeira, "Deve ter aumentado a carga ao longo das semanas");
        assertEquals(60.0, cargaPrimeira);
        assertEquals(75.0, cargaUltima);
    }

    // Métodos auxiliares
    private Exercicio criarExercicio(Usuario usuario, String nome, String descricao, String gif) {
        Exercicio exercicio = new Exercicio();
        exercicio.setUsuario(usuario);
        exercicio.setNome(nome);
        exercicio.setDescricao(descricao);
        exercicio.setCaminhoGif(gif);
        return exercicioDAO.salvar(exercicio);
    }

    private PlanoTreino criarPlano(Usuario usuario, String nome) {
        PlanoTreino plano = new PlanoTreino();
        plano.setUsuario(usuario);
        plano.setNome(nome);
        return planoTreinoDAO.salvar(plano);
    }

    private ItemPlanoTreino adicionarItemAoPlano(PlanoTreino plano, Exercicio exercicio, int carga, int repeticoes) {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setPlanoTreino(plano);
        item.setExercicio(exercicio);
        item.setCargaKg(carga);
        item.setRepeticoes(repeticoes);
        return itemPlanoTreinoDAO.salvar(item);
    }

    private SessaoTreino criarSessao(Usuario usuario, PlanoTreino plano) {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());
        return sessaoTreinoDAO.salvar(sessao);
    }

    private SessaoTreino criarSessaoComData(Usuario usuario, PlanoTreino plano, LocalDate data) {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(data);
        return sessaoTreinoDAO.salvar(sessao);
    }

    private ItemSessaoTreino registrarExecucao(SessaoTreino sessao, Exercicio exercicio, int repeticoes, double carga) {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setSessaoTreino(sessao);
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(repeticoes);
        item.setCargaRealizada(carga);
        return itemSessaoTreinoDAO.salvar(item);
    }

    private IndicadorBiomedico cadastrarIndicador(Usuario usuario, LocalDate data, double peso, double altura, double gordura, double massaMagra) {
        double imc = peso / Math.pow(altura / 100.0, 2);

        IndicadorBiomedico indicador = new IndicadorBiomedico();
        indicador.setUsuario(usuario);
        indicador.setDataRegistro(data);
        indicador.setPesoKg(peso);
        indicador.setAlturaCm(altura);
        indicador.setPercentualGordura(gordura);
        indicador.setPercentualMassaMagra(massaMagra);
        indicador.setImc(imc);

        return indicadorDAO.salvar(indicador);
    }
}

