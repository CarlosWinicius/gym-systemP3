package br.upe.integration;

import br.upe.controller.business.ExercicioService;
import br.upe.data.TipoUsuario;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.Usuario;
import br.upe.test.dao.TestExercicioDAO;
import br.upe.test.dao.TestUsuarioDAO;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o fluxo completo de exercícios
 * Integra ExercicioService + ExercicioDAO + banco H2 em memória
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExercicioIntegrationTest {

    private ExercicioService exercicioService;
    private TestExercicioDAO exercicioDAO;
    private TestUsuarioDAO usuarioDAO;
    private EntityManager em;
    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        em = TestConnectionFactory.getTestEntityManager();
        TestConnectionFactory.clearDatabase(em);

        exercicioDAO = new TestExercicioDAO();
        usuarioDAO = new TestUsuarioDAO();
        exercicioService = new ExercicioService(exercicioDAO, usuarioDAO);

        // Criar usuário de teste
        usuarioTeste = new Usuario();
        usuarioTeste.setNome("Usuário Teste");
        usuarioTeste.setEmail("teste@email.com");
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
//
    @Test
    @Order(1)
    @DisplayName("Integração: Deve cadastrar exercício e persistir no banco")
    void testCadastrarExercicioCompleto() {
        // Dado
        String nome = "Supino Reto";
        String descricao = "Exercício para peitoral";
        String caminhoGif = "/gif/supino.gif";

        // Quando
        Exercicio exercicioCadastrado = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), nome, descricao, caminhoGif
        );

        // Então
        assertNotNull(exercicioCadastrado);
        assertNotNull(exercicioCadastrado.getId());
        assertEquals(nome, exercicioCadastrado.getNome());
        assertEquals(descricao, exercicioCadastrado.getDescricao());
        assertEquals(caminhoGif, exercicioCadastrado.getCaminhoGif());
        assertEquals(usuarioTeste.getId(), exercicioCadastrado.getUsuario().getId());

        // Verifica persistência
        Optional<Exercicio> exercicioBuscado = exercicioDAO.buscarPorId(exercicioCadastrado.getId());
        assertTrue(exercicioBuscado.isPresent());
        assertEquals(nome, exercicioBuscado.get().getNome());
    }
//
    @Test
    @Order(2)
    @DisplayName("Integração: Não deve permitir exercícios com nome duplicado para o mesmo usuário")
    void testValidacaoNomeDuplicado() {
        // Cadastra primeiro exercício
        String nomeDuplicado = "Agachamento";
        exercicioService.cadastrarExercicio(usuarioTeste.getId(), nomeDuplicado, "Descrição 1", "/gif/1.gif");

        // Tenta cadastrar com mesmo nome (case insensitive)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(usuarioTeste.getId(), "agachamento", "Descrição 2", "/gif/2.gif");
        });

        assertTrue(exception.getMessage().contains("já possui um exercício com o nome"));
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve permitir mesmo nome de exercício para usuários diferentes")
    void testExerciciosMesmoNomeUsuariosDiferentes() {
        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        String nomeExercicio = "Flexão";

        // Usuário 1 cadastra
        Exercicio exercicio1 = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), nomeExercicio, "Descrição User 1", "/gif/1.gif"
        );

        // Usuário 2 cadastra com mesmo nome
        Exercicio exercicio2 = exercicioService.cadastrarExercicio(
                usuario2.getId(), nomeExercicio, "Descrição User 2", "/gif/2.gif"
        );

        assertNotNull(exercicio1);
        assertNotNull(exercicio2);
        assertNotEquals(exercicio1.getId(), exercicio2.getId());
        assertEquals(usuarioTeste.getId(), exercicio1.getUsuario().getId());
        assertEquals(usuario2.getId(), exercicio2.getUsuario().getId());
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve listar apenas exercícios do usuário")
    void testListarExerciciosDoUsuario() {
        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        // Cadastra exercícios para usuário 1
        exercicioService.cadastrarExercicio(usuarioTeste.getId(), "Exercício User1 - 1", "Desc", "/gif/1.gif");
        exercicioService.cadastrarExercicio(usuarioTeste.getId(), "Exercício User1 - 2", "Desc", "/gif/2.gif");

        // Cadastra exercício para usuário 2
        exercicioService.cadastrarExercicio(usuario2.getId(), "Exercício User2 - 1", "Desc", "/gif/3.gif");

        // Lista exercícios do usuário 1
        List<Exercicio> exerciciosUser1 = exercicioService.listarExerciciosDoUsuario(usuarioTeste.getId());
        assertEquals(2, exerciciosUser1.size());
        assertTrue(exerciciosUser1.stream().allMatch(e -> e.getUsuario().getId().equals(usuarioTeste.getId())));

        // Lista exercícios do usuário 2
        List<Exercicio> exerciciosUser2 = exercicioService.listarExerciciosDoUsuario(usuario2.getId());
        assertEquals(1, exerciciosUser2.size());
        assertEquals(usuario2.getId(), exerciciosUser2.get(0).getUsuario().getId());
    }
//
    @Test
    @Order(5)
    @DisplayName("Integração: Deve buscar exercício por nome do usuário")
    void testBuscarExercicioPorNome() {
        String nomeExercicio = "Rosca Direta";
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), nomeExercicio, "Para bíceps", "/gif/rosca.gif"
        );

        // Busca exata
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), nomeExercicio
        );
        assertTrue(encontrado.isPresent());
        assertEquals(cadastrado.getId(), encontrado.get().getId());

        // Busca case insensitive
        Optional<Exercicio> encontradoLowerCase = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), "rosca direta"
        );
        assertTrue(encontradoLowerCase.isPresent());

        // Busca inexistente
        Optional<Exercicio> naoEncontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), "Exercício Inexistente"
        );
        assertFalse(naoEncontrado.isPresent());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve deletar exercício por nome")
    void testDeletarExercicioPorNome() {
        // Cadastra exercício
        String nomeExercicio = "Exercício Para Deletar";
        exercicioService.cadastrarExercicio(usuarioTeste.getId(), nomeExercicio, "Descrição", "/gif/del.gif");

        // Verifica que existe
        List<Exercicio> antes = exercicioService.listarExerciciosDoUsuario(usuarioTeste.getId());
        assertTrue(antes.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        // Deleta
        boolean deletado = exercicioService.deletarExercicioPorNome(usuarioTeste.getId(), nomeExercicio);
        assertTrue(deletado);

        // Verifica que foi deletado
        List<Exercicio> depois = exercicioService.listarExerciciosDoUsuario(usuarioTeste.getId());
        assertFalse(depois.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        // Verifica persistência
        Optional<Exercicio> buscado = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), nomeExercicio
        );
        assertFalse(buscado.isPresent());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve atualizar exercício existente")
    void testAtualizarExercicio() {
        // Cadastra exercício original
        String nomeOriginal = "Exercício Original";
        Exercicio original = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), nomeOriginal, "Descrição Original", "/gif/original.gif"
        );

        // Atualiza
        String novoNome = "Exercício Atualizado";
        String novaDescricao = "Descrição Atualizada";
        String novoCaminhoGif = "/gif/atualizado.gif";

        exercicioService.atualizarExercicio(
                usuarioTeste.getId(), nomeOriginal, novoNome, novaDescricao, novoCaminhoGif
        );

        // Verifica atualização
        Optional<Exercicio> atualizado = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), novoNome
        );
        assertTrue(atualizado.isPresent());
        assertEquals(original.getId(), atualizado.get().getId());
        assertEquals(novoNome, atualizado.get().getNome());
        assertEquals(novaDescricao, atualizado.get().getDescricao());
        assertEquals(novoCaminhoGif, atualizado.get().getCaminhoGif());

        // Verifica que nome antigo não existe mais
        Optional<Exercicio> antigoNome = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), nomeOriginal
        );
        assertFalse(antigoNome.isPresent());
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Deve validar nome obrigatório no cadastro")
    void testValidacaoNomeObrigatorio() {
        // Nome vazio
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(usuarioTeste.getId(), "", "Descrição", "/gif/test.gif");
        });
        assertTrue(exception1.getMessage().contains("Nome do exercício não pode ser vazio"));

        // Nome null
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(usuarioTeste.getId(), null, "Descrição", "/gif/test.gif");
        });
        assertTrue(exception2.getMessage().contains("Nome do exercício não pode ser vazio"));

        // Nome só com espaços
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(usuarioTeste.getId(), "   ", "Descrição", "/gif/test.gif");
        });
        assertTrue(exception3.getMessage().contains("Nome do exercício não pode ser vazio"));
    }

    @Test
    @Order(9)
    @DisplayName("Integração: Não deve deletar exercício inexistente")
    void testDeletarExercicioInexistente() {
        boolean resultado = exercicioService.deletarExercicioPorNome(
                usuarioTeste.getId(), "Exercício Que Não Existe"
        );
        assertFalse(resultado);
    }

    @Test
    @Order(10)
    @DisplayName("Integração: Deve buscar exercício por ID global")
    void testBuscarExercicioPorIdGlobal() {
        // Cadastra exercício
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), "Exercício Teste ID", "Descrição", "/gif/test.gif"
        );

        // Busca por ID global
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioPorIdGlobal(
                cadastrado.getId()
        );
        assertTrue(encontrado.isPresent());
        assertEquals(cadastrado.getId(), encontrado.get().getId());
        assertEquals(cadastrado.getNome(), encontrado.get().getNome());

        // Busca ID inexistente
        Optional<Exercicio> naoEncontrado = exercicioService.buscarExercicioPorIdGlobal(99999);
        assertFalse(naoEncontrado.isPresent());
    }

    @Test
    @Order(11)
    @DisplayName("Integração: Deve trimmar nome do exercício no cadastro")
    void testTrimNomeExercicio() {
        String nomeComEspacos = "  Exercício Com Espaços  ";
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                usuarioTeste.getId(), nomeComEspacos, "Descrição", "/gif/test.gif"
        );

        assertEquals("Exercício Com Espaços", cadastrado.getNome());

        // Deve encontrar sem espaços
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                usuarioTeste.getId(), "Exercício Com Espaços"
        );
        assertTrue(encontrado.isPresent());
    }
}
