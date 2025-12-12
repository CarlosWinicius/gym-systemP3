// language: java
package br.upe.integration;

import br.upe.controller.business.ExercicioService;
import br.upe.data.dao.ExercicioDAO;
import br.upe.data.entities.Exercicio;
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
    private static final String TEST_CSV_PATH = "src/test/resources/data/exercicios_integration_test.csv";
    private static final int ID_USUARIO_TESTE = 1;
    // Usuários usados nos testes (1 e 2 aparecem na suite)
    private static final int[] TEST_USERS = {1, 2};

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get("src/test/resources/data"));
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));

        // Limpa exercícios pré-existentes dos usuários de teste para evitar colisões entre execuções
        ExercicioDAO dao = new ExercicioDAO();
        for (int userId : TEST_USERS) {
            try {
                List<Exercicio> lista = dao.buscarTodosDoUsuario(userId);
                if (lista != null) {
                    for (Exercicio e : lista) {
                        if (e != null && e.getId() != null) {
                            try {
                                dao.deletar(e.getId());
                            } catch (Exception ex) {
                                System.err.println("Falha ao deletar exercício id=" + e.getId() + ": " + ex.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Falha ao buscar exercícios do usuário " + userId + ": " + ex.getMessage());
            }
        }

        // Usa construtor sem parâmetros do service (ajuste caso a sua classe ExercicioService precise de outro construtor)
        exercicioService = new ExercicioService();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
    }

    @AfterAll
    static void cleanupDatabase() {
        ExercicioDAO dao = new ExercicioDAO();
        for (int userId : TEST_USERS) {
            try {
                List<Exercicio> lista = dao.buscarTodosDoUsuario(userId);
                if (lista != null) {
                    for (Exercicio e : lista) {
                        if (e != null && e.getId() != null) {
                            try {
                                dao.deletar(e.getId());
                            } catch (Exception ex) {
                                // Não falhar a suíte por um erro de limpeza; log mínimo
                                System.err.println("Falha ao deletar exercício id=" + e.getId() + ": " + ex.getMessage());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Falha ao buscar exercícios do usuário " + userId + ": " + ex.getMessage());
            }
        }

        // Remove arquivo de teste se ainda existir
        try {
            Files.deleteIfExists(Paths.get(TEST_CSV_PATH));
        } catch (IOException e) {
            System.err.println("Falha ao apagar arquivo de teste: " + e.getMessage());
        }
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
        assertNotEquals(0, exercicioCadastrado.getId());
        assertEquals(nome, exercicioCadastrado.getNome());
        assertEquals(descricao, exercicioCadastrado.getDescricao());
        assertEquals(caminhoGif, exercicioCadastrado.getCaminhoGif());
        assertEquals(ID_USUARIO_TESTE, exercicioCadastrado.getUsuario().getId());

        // Verifica persistência via service
        Optional<Exercicio> exercicioBuscado = exercicioService.buscarExercicioPorIdGlobal(exercicioCadastrado.getId());
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
        Exception exception = assertThrows(IllegalArgumentException.class, () -> exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "agachamento", "Descrição 2", "/gif/2.gif"));

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
        assertNotEquals(exercicio1.getId(), exercicio2.getId());
        assertEquals(1, exercicio1.getUsuario().getId());
        assertEquals(2, exercicio2.getUsuario().getId());
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
        assertTrue(exerciciosUser1.stream().allMatch(e -> e.getUsuario().getId() == 1));

        // Lista exercícios do usuário 2
        List<Exercicio> exerciciosUser2 = exercicioService.listarExerciciosDoUsuario(2);
        assertEquals(1, exerciciosUser2.size());
        assertEquals(2, exerciciosUser2.getFirst().getUsuario().getId());
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
        assertEquals(cadastrado.getId(), encontrado.get().getId());

        //Busca case insensitive
        Optional<Exercicio> encontradoLowerCase = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "rosca direta"
        );
        assertTrue(encontradoLowerCase.isPresent());

        //Busca inexistente
        Optional<Exercicio> naoEncontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "Exercício Inexistente"
        );
        assertFalse(naoEncontrado.isPresent());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve deletar exercício por nome")
    void testDeletarExercicioPorNome() {
        //Cadastra exercício
        String nomeExercicio = "Exercício Para Deletar";
        exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, nomeExercicio, "Descrição", "/gif/del.gif");

        //Verifica que existe
        List<Exercicio> antes = exercicioService.listarExerciciosDoUsuario(ID_USUARIO_TESTE);
        assertTrue(antes.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        //Deleta
        boolean deletado = exercicioService.deletarExercicioPorNome(ID_USUARIO_TESTE, nomeExercicio);
        assertTrue(deletado);

        //Verifica que foi deletado
        List<Exercicio> depois = exercicioService.listarExerciciosDoUsuario(ID_USUARIO_TESTE);
        assertFalse(depois.stream().anyMatch(e -> e.getNome().equals(nomeExercicio)));

        //Verifica persistência
        Optional<Exercicio> buscado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, nomeExercicio
        );
        assertFalse(buscado.isPresent());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve atualizar exercício existente")
    void testAtualizarExercicio() {
        //Cadastra exercício original
        String nomeOriginal = "Exercício Original";
        Exercicio original = exercicioService.cadastrarExercicio(
                ID_USUARIO_TESTE, nomeOriginal, "Descrição Original", "/gif/original.gif"
        );

        //Atualiza
        String novoNome = "Exercício Atualizado";
        String novaDescricao = "Descrição Atualizada";
        String novoCaminhoGif = "/gif/atualizado.gif";

        exercicioService.atualizarExercicio(
                ID_USUARIO_TESTE, nomeOriginal, novoNome, novaDescricao, novoCaminhoGif
        );

        //Verifica atualização
        Optional<Exercicio> atualizado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, novoNome
        );
        assertTrue(atualizado.isPresent());
        assertEquals(original.getId(), atualizado.get().getId());
        assertEquals(novoNome, atualizado.get().getNome());
        assertEquals(novaDescricao, atualizado.get().getDescricao());
        assertEquals(novoCaminhoGif, atualizado.get().getCaminhoGif());

        //Verifica que nome antigo não existe mais
        Optional<Exercicio> antigoNome = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, nomeOriginal
        );
        assertFalse(antigoNome.isPresent());
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Deve validar nome obrigatório no cadastro")
    void testValidacaoNomeObrigatorio() {
        //Nome vazio
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "", "Descrição", "/gif/test.gif"));
        assertTrue(exception1.getMessage().contains("Nome do exercício não pode ser vazio"));

        //Nome null
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, null, "Descrição", "/gif/test.gif"));
        assertTrue(exception2.getMessage().contains("Nome do exercício não pode ser vazio"));

        //Nome só com espaços
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "   ", "Descrição", "/gif/test.gif"));
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
        //Cadastra exercício
        Exercicio cadastrado = exercicioService.cadastrarExercicio(
                ID_USUARIO_TESTE, "Exercício Teste ID", "Descrição", "/gif/test.gif"
        );

        //Busca por ID global
        Optional<Exercicio> encontrado = exercicioService.buscarExercicioPorIdGlobal(
                cadastrado.getId()
        );
        assertTrue(encontrado.isPresent());
        assertEquals(cadastrado.getId(), encontrado.get().getId());
        assertEquals(cadastrado.getNome(), encontrado.get().getNome());

        //Busca ID inexistente
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

        Optional<Exercicio> encontrado = exercicioService.buscarExercicioDoUsuarioPorNome(
                ID_USUARIO_TESTE, "Exercício Com Espaços"
        );
        assertTrue(encontrado.isPresent());
    }
}
