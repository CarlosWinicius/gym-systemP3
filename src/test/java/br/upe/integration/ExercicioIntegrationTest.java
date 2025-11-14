package br.upe.integration;

import br.upe.controller.business.ExercicioService;
import br.upe.data.beans.Exercicio;
import br.upe.data.repository.IExercicioRepository;
import br.upe.data.repository.impl.ExercicioRepositoryImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o fluxo completo de exercícios
 * Integra ExercicioService + ExercicioRepositoryImpl + arquivo CSV
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExercicioIntegrationTest {

    private ExercicioService exercicioService;
    private IExercicioRepository exercicioRepository;
    private static final String TEST_CSV_PATH = "src/test/resources/data/exercicios_integration_test.csv";
    private static final int ID_USUARIO_TESTE = 1;

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("src/test/resources/data"));
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
        exercicioRepository = new ExercicioRepositoryImpl(TEST_CSV_PATH);
        exercicioService = new ExercicioService(exercicioRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
    }

    @Test
    @Order(1)
    @DisplayName("Integração: Deve cadastrar exercício e persistir no CSV")
    void testCadastrarExercicioCompleto() {
        // Dado
        String nome = "Supino Reto";
        String descricao = "Exercício para peitoral";
        String caminhoGif = "/gif/supino.gif";

        // Quando
        Exercicio exercicioCadastrado = exercicioService.cadastrarExercicio(
                ID_USUARIO_TESTE, nome, descricao, caminhoGif
        );

        // Então
        assertNotNull(exercicioCadastrado);
        assertNotEquals(0, exercicioCadastrado.getIdExercicio());
        assertEquals(nome, exercicioCadastrado.getNome());
        assertEquals(descricao, exercicioCadastrado.getDescricao());
        assertEquals(caminhoGif, exercicioCadastrado.getCaminhoGif());
        assertEquals(ID_USUARIO_TESTE, exercicioCadastrado.getIdUsuario());

        // Verifica persistência
        Optional<Exercicio> exercicioBuscado = exercicioRepository.buscarPorId(exercicioCadastrado.getIdExercicio());
        assertTrue(exercicioBuscado.isPresent());
        assertEquals(nome, exercicioBuscado.get().getNome());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Não deve permitir exercícios com nome duplicado para o mesmo usuário")
    void testValidacaoNomeDuplicado() {
        // Cadastra primeiro exercício
        String nomeDuplicado = "Agachamento";
        exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, nomeDuplicado, "Descrição 1", "/gif/1.gif");

        // Tenta cadastrar com mesmo nome (case insensitive)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "agachamento", "Descrição 2", "/gif/2.gif");
        });

        assertTrue(exception.getMessage().contains("já possui um exercício com o nome"));
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve permitir mesmo nome de exercício para usuários diferentes")
    void testExerciciosMesmoNomeUsuariosDiferentes() {
        String nomeExercicio = "Flexão";

        // Usuário 1 cadastra
        Exercicio exercicio1 = exercicioService.cadastrarExercicio(
                1, nomeExercicio, "Descrição User 1", "/gif/1.gif"
        );

        // Usuário 2 cadastra com mesmo nome
        Exercicio exercicio2 = exercicioService.cadastrarExercicio(
                2, nomeExercicio, "Descrição User 2", "/gif/2.gif"
        );

        assertNotNull(exercicio1);
        assertNotNull(exercicio2);
        assertNotEquals(exercicio1.getIdExercicio(), exercicio2.getIdExercicio());
        assertEquals(1, exercicio1.getIdUsuario());
        assertEquals(2, exercicio2.getIdUsuario());
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve listar apenas exercícios do usuário")
    void testListarExerciciosDoUsuario() {
        // Cadastra exercícios para usuário 1
        exercicioService.cadastrarExercicio(1, "Exercício User1 - 1", "Desc", "/gif/1.gif");
        exercicioService.cadastrarExercicio(1, "Exercício User1 - 2", "Desc", "/gif/2.gif");

        // Cadastra exercício para usuário 2
        exercicioService.cadastrarExercicio(2, "Exercício User2 - 1", "Desc", "/gif/3.gif");

        // Lista exercícios do usuário 1
        List<Exercicio> exerciciosUser1 = exercicioService.listarExerciciosDoUsuario(1);
        assertEquals(2, exerciciosUser1.size());
        assertTrue(exerciciosUser1.stream().allMatch(e -> e.getIdUsuario() == 1));

        // Lista exercícios do usuário 2
        List<Exercicio> exerciciosUser2 = exercicioService.listarExerciciosDoUsuario(2);
        assertEquals(1, exerciciosUser2.size());
        assertEquals(2, exerciciosUser2.get(0).getIdUsuario());
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve buscar exercício por nome do usuário")
    void testBuscarExercicioPorNome() {
        String nomeExercicio = "Rosca Direta";
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                ID_USUARIO_TESTE, nomeExercicio, "Para bíceps", "/gif/rosca.gif"
        );

        // Busca exata
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, nomeExercicio
        );
        assertTrue(encontrado.isPresent());
        assertEquals(cadastrado.getIdExercicio(), encontrado.get().getIdExercicio());

        // Busca case insensitive
        Optional<Exercicio> encontradoLowerCase = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "rosca direta"
        );
        assertTrue(encontradoLowerCase.isPresent());

        // Busca inexistente
        Optional<Exercicio> naoEncontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "Exercício Inexistente"
        );
        assertFalse(naoEncontrado.isPresent());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve deletar exercício por nome")
    void testDeletarExercicioPorNome() {
        // Cadastra exercício
        String nomeExercicio = "Exercício Para Deletar";
        exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, nomeExercicio, "Descrição", "/gif/del.gif");

        // Verifica que existe
        List<Exercicio> antes = exercicioService.listarExerciciosDoUsuario(ID_USUARIO_TESTE);
        assertTrue(antes.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        // Deleta
        boolean deletado = exercicioService.deletarExercicioPorNome(ID_USUARIO_TESTE, nomeExercicio);
        assertTrue(deletado);

        // Verifica que foi deletado
        List<Exercicio> depois = exercicioService.listarExerciciosDoUsuario(ID_USUARIO_TESTE);
        assertFalse(depois.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        // Verifica persistência
        Optional<Exercicio> buscado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, nomeExercicio
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
                ID_USUARIO_TESTE, nomeOriginal, "Descrição Original", "/gif/original.gif"
        );

        // Atualiza
        String novoNome = "Exercício Atualizado";
        String novaDescricao = "Descrição Atualizada";
        String novoCaminhoGif = "/gif/atualizado.gif";

        exercicioService.atualizarExercicio(
                ID_USUARIO_TESTE, nomeOriginal, novoNome, novaDescricao, novoCaminhoGif
        );

        // Verifica atualização
        Optional<Exercicio> atualizado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, novoNome
        );
        assertTrue(atualizado.isPresent());
        assertEquals(original.getIdExercicio(), atualizado.get().getIdExercicio());
        assertEquals(novoNome, atualizado.get().getNome());
        assertEquals(novaDescricao, atualizado.get().getDescricao());
        assertEquals(novoCaminhoGif, atualizado.get().getCaminhoGif());

        // Verifica que nome antigo não existe mais
        Optional<Exercicio> antigoNome = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, nomeOriginal
        );
        assertFalse(antigoNome.isPresent());
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Deve validar nome obrigatório no cadastro")
    void testValidacaoNomeObrigatorio() {
        // Nome vazio
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "", "Descrição", "/gif/test.gif");
        });
        assertTrue(exception1.getMessage().contains("Nome do exercício não pode ser vazio"));

        // Nome null
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, null, "Descrição", "/gif/test.gif");
        });
        assertTrue(exception2.getMessage().contains("Nome do exercício não pode ser vazio"));

        // Nome só com espaços
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "   ", "Descrição", "/gif/test.gif");
        });
        assertTrue(exception3.getMessage().contains("Nome do exercício não pode ser vazio"));
    }

    @Test
    @Order(9)
    @DisplayName("Integração: Não deve deletar exercício inexistente")
    void testDeletarExercicioInexistente() {
        boolean resultado = exercicioService.deletarExercicioPorNome(
                ID_USUARIO_TESTE, "Exercício Que Não Existe"
        );
        assertFalse(resultado);
    }

    @Test
    @Order(10)
    @DisplayName("Integração: Deve buscar exercício por ID global")
    void testBuscarExercicioPorIdGlobal() {
        // Cadastra exercício
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                ID_USUARIO_TESTE, "Exercício Teste ID", "Descrição", "/gif/test.gif"
        );

        // Busca por ID global
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioPorIdGlobal(
                cadastrado.getIdExercicio()
        );
        assertTrue(encontrado.isPresent());
        assertEquals(cadastrado.getIdExercicio(), encontrado.get().getIdExercicio());
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
                ID_USUARIO_TESTE, nomeComEspacos, "Descrição", "/gif/test.gif"
        );

        assertEquals("Exercício Com Espaços", cadastrado.getNome());

        // Deve encontrar sem espaços
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "Exercício Com Espaços"
        );
        assertTrue(encontrado.isPresent());
    }
}

