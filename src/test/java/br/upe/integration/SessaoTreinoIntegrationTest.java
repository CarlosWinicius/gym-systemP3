package br.upe.integration;

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
 * Testes de integração para o fluxo completo de sessões de treino
 * Integra SessaoTreinoService + DAOs + banco H2 em memória
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessaoTreinoIntegrationTest {

    private TestSessaoTreinoDAO sessaoTreinoDAO;
    private TestPlanoTreinoDAO planoTreinoDAO;
    private TestExercicioDAO exercicioDAO;
    private TestUsuarioDAO usuarioDAO;
    private TestItemPlanoTreinoDAO itemPlanoTreinoDAO;
    private TestItemSessaoTreinoDAO itemSessaoTreinoDAO;
    private EntityManager em;
    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        em = TestConnectionFactory.getTestEntityManager();
        TestConnectionFactory.clearDatabase(em);

        sessaoTreinoDAO = new TestSessaoTreinoDAO();
        planoTreinoDAO = new TestPlanoTreinoDAO();
        exercicioDAO = new TestExercicioDAO();
        usuarioDAO = new TestUsuarioDAO();
        itemPlanoTreinoDAO = new TestItemPlanoTreinoDAO();
        itemSessaoTreinoDAO = new TestItemSessaoTreinoDAO();

        // Criar usuário de teste
        usuarioTeste = new Usuario();
        usuarioTeste.setNome("Usuário Teste Sessões");
        usuarioTeste.setEmail("teste.sessoes@email.com");
        usuarioTeste.setSenha("senha123");
        usuarioTeste.setTipo(TipoUsuario.COMUM);
        usuarioTeste = usuarioDAO.salvar(usuarioTeste);
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
    @DisplayName("Integração: Deve iniciar sessão de treino baseada em plano")
    void testIniciarSessao() {
        // Cria plano
        PlanoTreino plano = criarPlano("Treino A");

        // Inicia sessão
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuarioTeste);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());
        sessao = sessaoTreinoDAO.salvar(sessao);

        // Verifica
        assertNotNull(sessao);
        assertNotNull(sessao.getId());
        assertEquals(usuarioTeste.getId(), sessao.getUsuario().getId());
        assertEquals(plano.getId(), sessao.getPlanoTreino().getId());
        assertEquals(LocalDate.now(), sessao.getDataSessao());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Deve registrar execuções de exercícios na sessão")
    void testRegistrarExecucoes() {
        // Setup: cria exercícios e plano
        Exercicio ex1 = criarExercicio("Supino");
        Exercicio ex2 = criarExercicio("Crucifixo");

        PlanoTreino plano = criarPlano("Treino Peito");
        adicionarItemAoPlano(plano, ex1, 80, 12);
        adicionarItemAoPlano(plano, ex2, 30, 15);

        // Inicia sessão
        SessaoTreino sessao = criarSessao(plano);

        // Registra execuções
        registrarExecucao(sessao, ex1, 12, 80.0);
        registrarExecucao(sessao, ex2, 15, 30.0);

        // Verifica
        List<ItemSessaoTreino> itensExecutados = itemSessaoTreinoDAO.listarPorSessao(sessao.getId());
        assertEquals(2, itensExecutados.size());

        ItemSessaoTreino item1 = itensExecutados.stream()
                .filter(i -> i.getExercicio().getId().equals(ex1.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(item1);
        assertEquals(12, item1.getRepeticoesRealizadas());
        assertEquals(80.0, item1.getCargaRealizada());

        ItemSessaoTreino item2 = itensExecutados.stream()
                .filter(i -> i.getExercicio().getId().equals(ex2.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(item2);
        assertEquals(15, item2.getRepeticoesRealizadas());
        assertEquals(30.0, item2.getCargaRealizada());
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve buscar sessões por usuário")
    void testBuscarSessoesPorUsuario() {
        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        // Cria planos
        PlanoTreino planoUser1 = criarPlano("Plano User1");
        PlanoTreino planoUser2 = criarPlanoParaUsuario("Plano User2", usuario2);

        // Cria sessões para usuário 1
        criarSessao(planoUser1);
        criarSessao(planoUser1);

        // Cria sessão para usuário 2
        SessaoTreino sessaoUser2 = new SessaoTreino();
        sessaoUser2.setUsuario(usuario2);
        sessaoUser2.setPlanoTreino(planoUser2);
        sessaoUser2.setDataSessao(LocalDate.now());
        sessaoTreinoDAO.salvar(sessaoUser2);

        // Verifica isolamento
        List<SessaoTreino> sessoesUser1 = sessaoTreinoDAO.buscarTodosDoUsuario(usuarioTeste.getId());
        assertEquals(2, sessoesUser1.size());
        assertTrue(sessoesUser1.stream().allMatch(s -> s.getUsuario().getId().equals(usuarioTeste.getId())));

        List<SessaoTreino> sessoesUser2 = sessaoTreinoDAO.buscarTodosDoUsuario(usuario2.getId());
        assertEquals(1, sessoesUser2.size());
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve buscar sessões por período")
    void testBuscarSessoesPorPeriodo() {
        PlanoTreino plano = criarPlano("Plano Período");

        // Cria sessões em diferentes datas
        criarSessaoComData(plano, LocalDate.of(2024, 1, 15));
        criarSessaoComData(plano, LocalDate.of(2024, 2, 15));
        criarSessaoComData(plano, LocalDate.of(2024, 3, 15));
        criarSessaoComData(plano, LocalDate.of(2024, 4, 15));

        // Busca por período jan-fev
        List<SessaoTreino> sessoesPeriodo = sessaoTreinoDAO.buscarPorPeriodo(
                usuarioTeste.getId(),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 2, 28)
        );

        assertEquals(2, sessoesPeriodo.size());
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve comparar execução com planejado")
    void testCompararExecucaoComPlanejado() {
        // Setup
        Exercicio exercicio = criarExercicio("Agachamento");
        PlanoTreino plano = criarPlano("Treino Pernas");
        ItemPlanoTreino itemPlanejado = adicionarItemAoPlano(plano, exercicio, 100, 10);

        // Executa com valores diferentes
        SessaoTreino sessao = criarSessao(plano);
        ItemSessaoTreino itemExecutado = registrarExecucao(sessao, exercicio, 12, 110.0);

        // Compara
        List<ItemPlanoTreino> itensPlanejados = itemPlanoTreinoDAO.listarPorPlano(plano.getId());
        List<ItemSessaoTreino> itensExecutados = itemSessaoTreinoDAO.listarPorSessao(sessao.getId());

        assertEquals(1, itensPlanejados.size());
        assertEquals(1, itensExecutados.size());

        ItemPlanoTreino planejado = itensPlanejados.get(0);
        ItemSessaoTreino executado = itensExecutados.get(0);

        // Verifica diferenças
        assertEquals(100, planejado.getCargaKg());
        assertEquals(110.0, executado.getCargaRealizada());
        assertTrue(executado.getCargaRealizada() > planejado.getCargaKg());

        assertEquals(10, planejado.getRepeticoes());
        assertEquals(12, executado.getRepeticoesRealizadas());
        assertTrue(executado.getRepeticoesRealizadas() > planejado.getRepeticoes());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve deletar sessão e seus itens")
    void testDeletarSessao() {
        // Setup
        Exercicio exercicio = criarExercicio("Exercício Teste");
        PlanoTreino plano = criarPlano("Plano Teste");
        SessaoTreino sessao = criarSessao(plano);
        registrarExecucao(sessao, exercicio, 10, 50.0);

        // Verifica que existem
        assertTrue(sessaoTreinoDAO.buscarPorId(sessao.getId()).isPresent());
        assertEquals(1, itemSessaoTreinoDAO.listarPorSessao(sessao.getId()).size());

        // Deleta itens primeiro
        List<ItemSessaoTreino> itens = itemSessaoTreinoDAO.listarPorSessao(sessao.getId());
        for (ItemSessaoTreino item : itens) {
            itemSessaoTreinoDAO.deletar(item.getId());
        }

        // Deleta sessão
        sessaoTreinoDAO.deletar(sessao.getId());

        // Verifica deleção
        assertFalse(sessaoTreinoDAO.buscarPorId(sessao.getId()).isPresent());
        assertEquals(0, itemSessaoTreinoDAO.listarPorSessao(sessao.getId()).size());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve manter histórico completo de sessões")
    void testHistoricoSessoes() {
        // Setup: cria plano com exercícios
        Exercicio ex1 = criarExercicio("Supino");
        Exercicio ex2 = criarExercicio("Rosca");
        PlanoTreino plano = criarPlano("Treino Completo");
        adicionarItemAoPlano(plano, ex1, 60, 12);
        adicionarItemAoPlano(plano, ex2, 20, 15);

        // Simula 3 semanas de treino
        // Semana 1
        SessaoTreino sessao1 = criarSessaoComData(plano, LocalDate.of(2024, 1, 8));
        registrarExecucao(sessao1, ex1, 12, 60.0);
        registrarExecucao(sessao1, ex2, 15, 20.0);

        // Semana 2 - aumentou carga
        SessaoTreino sessao2 = criarSessaoComData(plano, LocalDate.of(2024, 1, 15));
        registrarExecucao(sessao2, ex1, 12, 65.0);
        registrarExecucao(sessao2, ex2, 15, 22.0);

        // Semana 3 - aumentou carga novamente
        SessaoTreino sessao3 = criarSessaoComData(plano, LocalDate.of(2024, 1, 22));
        registrarExecucao(sessao3, ex1, 12, 70.0);
        registrarExecucao(sessao3, ex2, 15, 24.0);

        // Verifica histórico
        List<SessaoTreino> historico = sessaoTreinoDAO.buscarTodosDoUsuario(usuarioTeste.getId());
        assertEquals(3, historico.size());

        // Verifica evolução de carga (busca itens de cada sessão)
        List<ItemSessaoTreino> itensSessao1 = itemSessaoTreinoDAO.listarPorSessao(sessao1.getId());
        List<ItemSessaoTreino> itensSessao3 = itemSessaoTreinoDAO.listarPorSessao(sessao3.getId());

        double cargaSupinoSemana1 = itensSessao1.stream()
                .filter(i -> i.getExercicio().getId().equals(ex1.getId()))
                .findFirst()
                .map(ItemSessaoTreino::getCargaRealizada)
                .orElse(0.0);

        double cargaSupinoSemana3 = itensSessao3.stream()
                .filter(i -> i.getExercicio().getId().equals(ex1.getId()))
                .findFirst()
                .map(ItemSessaoTreino::getCargaRealizada)
                .orElse(0.0);

        assertEquals(60.0, cargaSupinoSemana1);
        assertEquals(70.0, cargaSupinoSemana3);
        assertTrue(cargaSupinoSemana3 > cargaSupinoSemana1, "Deve ter evolução de carga");
    }

    // Métodos auxiliares
    private Exercicio criarExercicio(String nome) {
        Exercicio exercicio = new Exercicio();
        exercicio.setUsuario(usuarioTeste);
        exercicio.setNome(nome);
        exercicio.setDescricao("Descrição do " + nome);
        exercicio.setCaminhoGif("/gif/test.gif");
        return exercicioDAO.salvar(exercicio);
    }

    private PlanoTreino criarPlano(String nome) {
        PlanoTreino plano = new PlanoTreino();
        plano.setUsuario(usuarioTeste);
        plano.setNome(nome);
        return planoTreinoDAO.salvar(plano);
    }

    private PlanoTreino criarPlanoParaUsuario(String nome, Usuario usuario) {
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

    private SessaoTreino criarSessao(PlanoTreino plano) {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuarioTeste);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());
        return sessaoTreinoDAO.salvar(sessao);
    }

    private SessaoTreino criarSessaoComData(PlanoTreino plano, LocalDate data) {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuarioTeste);
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
}

