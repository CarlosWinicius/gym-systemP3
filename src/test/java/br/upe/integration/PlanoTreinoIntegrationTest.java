package br.upe.integration;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.*;
import br.upe.test.dao.*;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o fluxo completo de planos de treino
 * Integra PlanoTreinoService + DAOs + banco H2 em memória
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlanoTreinoIntegrationTest {

    private TestPlanoTreinoDAO planoTreinoDAO;
    private TestExercicioDAO exercicioDAO;
    private TestUsuarioDAO usuarioDAO;
    private TestItemPlanoTreinoDAO itemPlanoTreinoDAO;
    private EntityManager em;
    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        em = TestConnectionFactory.getTestEntityManager();
        TestConnectionFactory.clearDatabase(em);

        planoTreinoDAO = new TestPlanoTreinoDAO();
        exercicioDAO = new TestExercicioDAO();
        usuarioDAO = new TestUsuarioDAO();
        itemPlanoTreinoDAO = new TestItemPlanoTreinoDAO();

        // Criar usuário de teste
        usuarioTeste = new Usuario();
        usuarioTeste.setNome("Usuário Teste Planos");
        usuarioTeste.setEmail("teste.planos@email.com");
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
    @DisplayName("Integração: Deve criar plano de treino e persistir")
    void testCriarPlanoCompleto() {
        // Dado
        String nomePlano = "Treino de Peito";

        // Quando
        PlanoTreino novoPlano = new PlanoTreino();
        novoPlano.setUsuario(usuarioTeste);
        novoPlano.setNome(nomePlano);
        PlanoTreino planoCriado = planoTreinoDAO.salvar(novoPlano);

        // Então
        assertNotNull(planoCriado);
        assertNotNull(planoCriado.getId());
        assertEquals(nomePlano, planoCriado.getNome());
        assertEquals(usuarioTeste.getId(), planoCriado.getUsuario().getId());

        // Verifica persistência
        Optional<PlanoTreino> planoBuscado = planoTreinoDAO.buscarPorId(planoCriado.getId());
        assertTrue(planoBuscado.isPresent());
        assertEquals(nomePlano, planoBuscado.get().getNome());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Deve validar nome de plano duplicado para o mesmo usuário")
    void testValidacaoNomePlanoDuplicado() {
        String nomeDuplicado = "Treino ABC";

        // Cria primeiro plano
        PlanoTreino plano1 = new PlanoTreino();
        plano1.setUsuario(usuarioTeste);
        plano1.setNome(nomeDuplicado);
        planoTreinoDAO.salvar(plano1);

        // Verifica se plano com mesmo nome já existe
        Optional<PlanoTreino> existente = planoTreinoDAO.buscarPorNomeEUsuario(usuarioTeste.getId(), nomeDuplicado);
        assertTrue(existente.isPresent());
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve adicionar exercício ao plano")
    void testAdicionarExercicioAoPlano() {
        // Cria exercício
        Exercicio exercicio = new Exercicio();
        exercicio.setUsuario(usuarioTeste);
        exercicio.setNome("Supino Reto");
        exercicio.setDescricao("Exercício para peitoral");
        exercicio.setCaminhoGif("/gif/supino.gif");
        exercicio = exercicioDAO.salvar(exercicio);

        // Cria plano
        PlanoTreino plano = new PlanoTreino();
        plano.setUsuario(usuarioTeste);
        plano.setNome("Treino Peito");
        plano = planoTreinoDAO.salvar(plano);

        // Adiciona exercício ao plano
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setPlanoTreino(plano);
        item.setExercicio(exercicio);
        item.setCargaKg(80);
        item.setRepeticoes(12);
        itemPlanoTreinoDAO.salvar(item);

        // Verifica adição
        List<ItemPlanoTreino> itens = itemPlanoTreinoDAO.listarPorPlano(plano.getId());
        assertEquals(1, itens.size());
        assertEquals(exercicio.getId(), itens.get(0).getExercicio().getId());
        assertEquals(80, itens.get(0).getCargaKg());
        assertEquals(12, itens.get(0).getRepeticoes());
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve criar plano completo com múltiplos exercícios")
    void testCriarPlanoComMultiplosExercicios() {
        // Cria exercícios
        Exercicio ex1 = criarExercicio("Supino", "Desc");
        Exercicio ex2 = criarExercicio("Crucifixo", "Desc");
        Exercicio ex3 = criarExercicio("Pullover", "Desc");

        // Cria plano
        PlanoTreino plano = new PlanoTreino();
        plano.setUsuario(usuarioTeste);
        plano.setNome("Treino Completo");
        plano = planoTreinoDAO.salvar(plano);

        // Adiciona exercícios
        adicionarItemAoPlano(plano, ex1, 80, 12);
        adicionarItemAoPlano(plano, ex2, 30, 15);
        adicionarItemAoPlano(plano, ex3, 20, 15);

        // Verifica
        List<ItemPlanoTreino> itens = itemPlanoTreinoDAO.listarPorPlano(plano.getId());
        assertEquals(3, itens.size());
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve permitir mesmo nome de plano para usuários diferentes")
    void testPlanosMesmoNomeUsuariosDiferentes() {
        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        String nomePlano = "Treino A";

        // Usuário 1 cria plano
        PlanoTreino plano1 = new PlanoTreino();
        plano1.setUsuario(usuarioTeste);
        plano1.setNome(nomePlano);
        plano1 = planoTreinoDAO.salvar(plano1);

        // Usuário 2 cria plano com mesmo nome
        PlanoTreino plano2 = new PlanoTreino();
        plano2.setUsuario(usuario2);
        plano2.setNome(nomePlano);
        plano2 = planoTreinoDAO.salvar(plano2);

        assertNotNull(plano1.getId());
        assertNotNull(plano2.getId());
        assertNotEquals(plano1.getId(), plano2.getId());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve listar apenas planos do usuário")
    void testListarPlanosDoUsuario() {
        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        // Cadastra planos para usuário 1
        criarPlano("Plano A", usuarioTeste);
        criarPlano("Plano B", usuarioTeste);

        // Cadastra plano para usuário 2
        criarPlano("Plano C", usuario2);

        // Lista planos do usuário 1
        List<PlanoTreino> planosUser1 = planoTreinoDAO.buscarTodosDoUsuario(usuarioTeste.getId());
        assertEquals(2, planosUser1.size());
        assertTrue(planosUser1.stream().allMatch(p -> p.getUsuario().getId().equals(usuarioTeste.getId())));

        // Lista planos do usuário 2
        List<PlanoTreino> planosUser2 = planoTreinoDAO.buscarTodosDoUsuario(usuario2.getId());
        assertEquals(1, planosUser2.size());
        assertEquals(usuario2.getId(), planosUser2.get(0).getUsuario().getId());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve atualizar exercício no plano")
    void testAtualizarExercicioNoPlano() {
        // Setup
        Exercicio exercicio = criarExercicio("Agachamento", "Exercício para pernas");
        PlanoTreino plano = criarPlano("Treino Pernas", usuarioTeste);
        ItemPlanoTreino item = adicionarItemAoPlano(plano, exercicio, 80, 10);

        // Atualiza carga e repetições
        item.setCargaKg(100);
        item.setRepeticoes(12);
        itemPlanoTreinoDAO.editar(item);

        // Verifica atualização
        List<ItemPlanoTreino> itens = itemPlanoTreinoDAO.listarPorPlano(plano.getId());
        assertEquals(1, itens.size());
        assertEquals(100, itens.get(0).getCargaKg());
        assertEquals(12, itens.get(0).getRepeticoes());
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Deve remover exercício do plano")
    void testRemoverExercicioDoPlano() {
        // Setup - cria dados específicos para este teste
        Exercicio ex1 = criarExercicio("Exercício Para Remover", "Desc");
        PlanoTreino plano = criarPlano("Treino Para Teste Remoção", usuarioTeste);
        ItemPlanoTreino item = adicionarItemAoPlano(plano, ex1, 50, 10);

        Integer itemId = item.getId();
        assertNotNull(itemId, "Item deve ter ID após salvar");

        // Verifica que o item existe
        assertTrue(itemPlanoTreinoDAO.buscarPorId(itemId).isPresent(), "Item deve existir antes de deletar");

        // Remove o item
        itemPlanoTreinoDAO.deletar(itemId);

        // Verifica que item foi removido
        Optional<ItemPlanoTreino> itemDeletado = itemPlanoTreinoDAO.buscarPorId(itemId);
        assertFalse(itemDeletado.isPresent(), "Item deve ter sido deletado do banco");
    }

    @Test
    @Order(9)
    @DisplayName("Integração: Deve buscar plano por nome e usuário")
    void testBuscarPlanoPorNomeEUsuario() {
        String nomePlano = "Treino Específico";
        PlanoTreino plano = criarPlano(nomePlano, usuarioTeste);

        // Busca exata
        Optional<PlanoTreino> encontrado = planoTreinoDAO.buscarPorNomeEUsuario(usuarioTeste.getId(), nomePlano);
        assertTrue(encontrado.isPresent());
        assertEquals(plano.getId(), encontrado.get().getId());

        // Busca inexistente
        Optional<PlanoTreino> naoEncontrado = planoTreinoDAO.buscarPorNomeEUsuario(usuarioTeste.getId(), "Plano Inexistente");
        assertFalse(naoEncontrado.isPresent());
    }

    @Test
    @Order(10)
    @DisplayName("Integração: Deve deletar plano completo")
    void testDeletarPlanoCompleto() {
        // Setup
        Exercicio exercicio = criarExercicio("Exercício Teste", "Desc");
        PlanoTreino plano = criarPlano("Plano Para Deletar", usuarioTeste);
        adicionarItemAoPlano(plano, exercicio, 50, 10);

        // Deleta itens primeiro (respeitando FK)
        List<ItemPlanoTreino> itens = itemPlanoTreinoDAO.listarPorPlano(plano.getId());
        for (ItemPlanoTreino item : itens) {
            itemPlanoTreinoDAO.deletar(item.getId());
        }

        // Deleta plano
        planoTreinoDAO.deletar(plano.getId());

        // Verifica deleção
        Optional<PlanoTreino> planoApagado = planoTreinoDAO.buscarPorId(plano.getId());
        assertFalse(planoApagado.isPresent());
    }

    // Métodos auxiliares
    private Exercicio criarExercicio(String nome, String descricao) {
        Exercicio exercicio = new Exercicio();
        exercicio.setUsuario(usuarioTeste);
        exercicio.setNome(nome);
        exercicio.setDescricao(descricao);
        exercicio.setCaminhoGif("/gif/test.gif");
        return exercicioDAO.salvar(exercicio);
    }

    private PlanoTreino criarPlano(String nome, Usuario usuario) {
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
}

