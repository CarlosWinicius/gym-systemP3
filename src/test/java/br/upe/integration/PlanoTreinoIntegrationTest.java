//package br.upe.integration;
//
//import br.upe.controller.business.ExercicioService;
//import br.upe.controller.business.PlanoTreinoService;
//import br.upe.data.beans.Exercicio;
//import br.upe.data.beans.ItemPlanoTreino;
//import br.upe.data.beans.PlanoTreino;
//import br.upe.data.repository.IExercicioRepository;
//import br.upe.data.repository.IPlanoTreinoRepository;
//import br.upe.data.repository.impl.ExercicioRepositoryImpl;
//import br.upe.data.repository.impl.PlanoTreinoRepositoryImpl;
//import org.junit.jupiter.api.*;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Testes de integração para o fluxo completo de planos de treino
// * Integra PlanoTreinoService + PlanoTreinoRepositoryImpl + ExercicioService + arquivos CSV
// */
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class PlanoTreinoIntegrationTest {
//
//    private PlanoTreinoService planoTreinoService;
//    private ExercicioService exercicioService;
//    private IPlanoTreinoRepository planoTreinoRepository;
//    private IExercicioRepository exercicioRepository;
//
//    private static final String TEST_PLANO_CSV_PATH = "src/test/resources/data/planos_integration_test.csv";
//    private static final String TEST_EXERCICIO_CSV_PATH = "src/test/resources/data/exercicios_plano_integration_test.csv";
//    private static final int ID_USUARIO_TESTE = 1;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        Files.createDirectories(Paths.get("src/test/resources/data"));
//        Files.deleteIfExists(Paths.get(TEST_PLANO_CSV_PATH));
//        Files.deleteIfExists(Paths.get(TEST_EXERCICIO_CSV_PATH));
//
//        planoTreinoRepository = new PlanoTreinoRepositoryImpl(TEST_PLANO_CSV_PATH);
//        exercicioRepository = new ExercicioRepositoryImpl(TEST_EXERCICIO_CSV_PATH);
//
//        planoTreinoService = new PlanoTreinoService(planoTreinoRepository, exercicioRepository);
//        exercicioService = new ExercicioService(exercicioRepository);
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        Files.deleteIfExists(Paths.get(TEST_PLANO_CSV_PATH));
//        Files.deleteIfExists(Paths.get(TEST_EXERCICIO_CSV_PATH));
//    }
//
//    @Test
//    @Order(1)
//    @DisplayName("Integração: Deve criar plano de treino e persistir")
//    void testCriarPlanoCompleto() {
//        // Dado
//        String nomePlano = "Treino de Peito";
//
//        // Quando
//        PlanoTreino planoCriado = planoTreinoService.criarPlano(ID_USUARIO_TESTE, nomePlano);
//
//        // Então
//        assertNotNull(planoCriado);
//        assertNotEquals(0, planoCriado.getIdPlano());
//        assertEquals(nomePlano, planoCriado.getNome());
//        assertEquals(ID_USUARIO_TESTE, planoCriado.getIdUsuario());
//        assertNotNull(planoCriado.getItensTreino());
//        assertTrue(planoCriado.getItensTreino().isEmpty());
//
//        // Verifica persistência
//        Optional<PlanoTreino> planoBuscado = planoTreinoRepository.buscarPorId(planoCriado.getIdPlano());
//        assertTrue(planoBuscado.isPresent());
//        assertEquals(nomePlano, planoBuscado.get().getNome());
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("Integração: Não deve permitir planos com nome duplicado para o mesmo usuário")
//    void testValidacaoNomePlanoDuplicado() {
//        String nomeDuplicado = "Treino ABC";
//
//        // Cria primeiro plano
//        planoTreinoService.criarPlano(ID_USUARIO_TESTE, nomeDuplicado);
//
//        // Tenta criar com mesmo nome
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.criarPlano(ID_USUARIO_TESTE, nomeDuplicado);
//        });
//
//        assertTrue(exception.getMessage().contains("já possui um plano com o nome"));
//    }
//
//    @Test
//    @Order(3)
//    @DisplayName("Integração: Deve adicionar exercício ao plano")
//    void testAdicionarExercicioAoPlano() {
//        // Cria exercício
//        Exercicio exercicio = exercicioService.cadastrarExercicio(
//                ID_USUARIO_TESTE, "Supino Reto", "Exercício para peitoral", "/gif/supino.gif"
//        );
//
//        // Cria plano
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Peito");
//
//        // Adiciona exercício ao plano
//        int carga = 80;
//        int repeticoes = 12;
//        planoTreinoService.adicionarExercicioAoPlano(
//                ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), carga, repeticoes
//        );
//
//        // Verifica adição
//        Optional<PlanoTreino> planoAtualizado = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, plano.getNome()
//        );
//        assertTrue(planoAtualizado.isPresent());
//        assertEquals(1, planoAtualizado.get().getItensTreino().size());
//
//        ItemPlanoTreino item = planoAtualizado.get().getItensTreino().get(0);
//        assertEquals(exercicio.getIdExercicio(), item.getIdExercicio());
//        assertEquals(carga, item.getCargaKg());
//        assertEquals(repeticoes, item.getRepeticoes());
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("Integração: Deve criar plano completo com múltiplos exercícios")
//    void testCriarPlanoComMultiplosExercicios() {
//        // Cria exercícios
//        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Supino", "Desc", "/gif/1.gif");
//        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Crucifixo", "Desc", "/gif/2.gif");
//        Exercicio ex3 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Pullover", "Desc", "/gif/3.gif");
//
//        // Cria plano
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Completo");
//
//        // Adiciona exercícios
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 80, 12);
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 30, 15);
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex3.getIdExercicio(), 20, 15);
//
//        // Verifica
//        Optional<PlanoTreino> planoCompleto = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, plano.getNome()
//        );
//        assertTrue(planoCompleto.isPresent());
//        assertEquals(3, planoCompleto.get().getItensTreino().size());
//
//        // Verifica persistência
//        Optional<PlanoTreino> planoRecarregado = planoTreinoRepository.buscarPorId(plano.getIdPlano());
//        assertTrue(planoRecarregado.isPresent());
//        assertEquals(3, planoRecarregado.get().getItensTreino().size());
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("Integração: Não deve adicionar exercício duplicado ao plano")
//    void testValidacaoExercicioDuplicadoNoPlano() {
//        // Cria exercício e plano
//        Exercicio exercicio = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Agachamento", "Desc", "/gif/ag.gif");
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Treino Pernas");
//
//        // Adiciona primeira vez
//        planoTreinoService.adicionarExercicioAoPlano(
//                ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 100, 10
//        );
//
//        // Tenta adicionar novamente
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.adicionarExercicioAoPlano(
//                    ID_USUARIO_TESTE, plano.getNome(), exercicio.getIdExercicio(), 110, 8
//            );
//        });
//
//        assertTrue(exception.getMessage().contains("Exercício já adicionado a este plano"));
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("Integração: Deve remover exercício do plano")
//    void testRemoverExercicioDoPlano() {
//        // Setup
//        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Ex1", "Desc", "/gif/1.gif");
//        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Ex2", "Desc", "/gif/2.gif");
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Teste");
//
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 50, 10);
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 60, 12);
//
//        // Remove exercício 1
//        planoTreinoService.removerExercicioDoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio());
//
//        // Verifica remoção
//        Optional<PlanoTreino> planoAtualizado = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, plano.getNome()
//        );
//        assertTrue(planoAtualizado.isPresent());
//        assertEquals(1, planoAtualizado.get().getItensTreino().size());
//        assertEquals(ex2.getIdExercicio(), planoAtualizado.get().getItensTreino().get(0).getIdExercicio());
//    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Integração: Deve listar todos os planos do usuário")
//    void testListarMeusPlanos() {
//        // Cria planos para usuário 1
//        planoTreinoService.criarPlano(1, "Plano A");
//        planoTreinoService.criarPlano(1, "Plano B");
//        planoTreinoService.criarPlano(1, "Plano C");
//
//        // Cria plano para usuário 2
//        planoTreinoService.criarPlano(2, "Plano User2");
//
//        // Lista planos do usuário 1
//        List<PlanoTreino> planosUser1 = planoTreinoService.listarMeusPlanos(1);
//        assertEquals(3, planosUser1.size());
//        assertTrue(planosUser1.stream().allMatch(p -> p.getIdUsuario() == 1));
//
//        // Lista planos do usuário 2
//        List<PlanoTreino> planosUser2 = planoTreinoService.listarMeusPlanos(2);
//        assertEquals(1, planosUser2.size());
//        assertEquals(2, planosUser2.get(0).getIdUsuario());
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("Integração: Deve editar nome do plano")
//    void testEditarNomePlano() {
//        // Cria plano
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Nome Original");
//
//        // Edita nome
//        String novoNome = "Nome Editado";
//        planoTreinoService.editarPlano(ID_USUARIO_TESTE, "Nome Original", novoNome);
//
//        // Verifica edição
//        Optional<PlanoTreino> planoEditado = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, novoNome
//        );
//        assertTrue(planoEditado.isPresent());
//        assertEquals(plano.getIdPlano(), planoEditado.get().getIdPlano());
//        assertEquals(novoNome, planoEditado.get().getNome());
//
//        // Verifica que nome antigo não existe
//        Optional<PlanoTreino> nomeAntigo = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, "Nome Original"
//        );
//        assertFalse(nomeAntigo.isPresent());
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("Integração: Não deve adicionar exercício de outro usuário ao plano")
//    void testValidacaoExercicioOutroUsuario() {
//        // Usuário 1 cria exercício
//        Exercicio exercicioUser1 = exercicioService.cadastrarExercicio(1, "Exercício User1", "Desc", "/gif/1.gif");
//
//        // Usuário 2 cria plano
//        PlanoTreino planoUser2 = planoTreinoService.criarPlano(2, "Plano User2");
//
//        // Tenta adicionar exercício do usuário 1 ao plano do usuário 2
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.adicionarExercicioAoPlano(
//                    2, planoUser2.getNome(), exercicioUser1.getIdExercicio(), 50, 10
//            );
//        });
//
//        assertTrue(exception.getMessage().contains("não encontrado ou não pertence a você"));
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("Integração: Deve validar nome obrigatório do plano")
//    void testValidacaoNomeObrigatorio() {
//        // Nome vazio
//        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.criarPlano(ID_USUARIO_TESTE, "");
//        });
//        assertTrue(exception1.getMessage().contains("Nome do plano não pode ser vazio"));
//
//        // Nome null
//        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.criarPlano(ID_USUARIO_TESTE, null);
//        });
//        assertTrue(exception2.getMessage().contains("Nome do plano não pode ser vazio"));
//    }
//
//    @Test
//    @Order(11)
//    @DisplayName("Integração: Deve remover exercício inexistente e lançar exceção")
//    void testRemoverExercicioInexistente() {
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Vazio");
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.removerExercicioDoPlano(ID_USUARIO_TESTE, plano.getNome(), 99999);
//        });
//
//        assertTrue(exception.getMessage().contains("não encontrado neste plano"));
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("Integração: Fluxo completo - criar, adicionar exercícios, editar e remover")
//    void testFluxoCompletoPlanoTreino() {
//        // 1. Criar exercícios
//        Exercicio ex1 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Exercício 1", "Desc", "/gif/1.gif");
//        Exercicio ex2 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Exercício 2", "Desc", "/gif/2.gif");
//        Exercicio ex3 = exercicioService.cadastrarExercicio(ID_USUARIO_TESTE, "Exercício 3", "Desc", "/gif/3.gif");
//
//        // 2. Criar plano
//        PlanoTreino plano = planoTreinoService.criarPlano(ID_USUARIO_TESTE, "Plano Fluxo Completo");
//        assertNotNull(plano);
//
//        // 3. Adicionar exercícios
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex1.getIdExercicio(), 80, 10);
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio(), 60, 12);
//        planoTreinoService.adicionarExercicioAoPlano(ID_USUARIO_TESTE, plano.getNome(), ex3.getIdExercicio(), 40, 15);
//
//        // 4. Verificar adições
//        Optional<PlanoTreino> planoComExercicios = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, plano.getNome()
//        );
//        assertTrue(planoComExercicios.isPresent());
//        assertEquals(3, planoComExercicios.get().getItensTreino().size());
//
//        // 5. Remover um exercício
//        planoTreinoService.removerExercicioDoPlano(ID_USUARIO_TESTE, plano.getNome(), ex2.getIdExercicio());
//
//        // 6. Editar nome do plano
//        String novoNome = "Plano Fluxo Editado";
//        planoTreinoService.editarPlano(ID_USUARIO_TESTE, plano.getNome(), novoNome);
//
//        // 7. Verificar estado final
//        Optional<PlanoTreino> planoFinal = planoTreinoService.buscarPlanoPorNomeEUsuario(
//                ID_USUARIO_TESTE, novoNome
//        );
//        assertTrue(planoFinal.isPresent());
//        assertEquals(novoNome, planoFinal.get().getNome());
//        assertEquals(2, planoFinal.get().getItensTreino().size());
//        assertFalse(planoFinal.get().getItensTreino().stream()
//                .anyMatch(item -> item.getIdExercicio() == ex2.getIdExercicio()));
//
//        // 8. Verificar persistência
//        Optional<PlanoTreino> planoRecarregado = planoTreinoRepository.buscarPorId(plano.getIdPlano());
//        assertTrue(planoRecarregado.isPresent());
//        assertEquals(novoNome, planoRecarregado.get().getNome());
//        assertEquals(2, planoRecarregado.get().getItensTreino().size());
//    }
//}
//
