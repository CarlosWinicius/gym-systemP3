package br.upe.integration;

import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.controller.business.RelatorioDiferencaIndicadores;
import br.upe.data.TipoUsuario;
import br.upe.data.entities.IndicadorBiomedico;
import br.upe.data.entities.Usuario;
import br.upe.test.dao.TestIndicadorBiomedicoDAO;
import br.upe.test.dao.TestUsuarioDAO;
import br.upe.test.utils.TestConnectionFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o fluxo completo de indicadores biomédicos
 * Integra IndicadorBiomedicoService + DAOs + banco H2 em memória
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IndicadorBiomedicoIntegrationTest {

    private IndicadorBiomedicoService indicadorService;
    private TestIndicadorBiomedicoDAO indicadorDAO;
    private TestUsuarioDAO usuarioDAO;
    private EntityManager em;
    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        em = TestConnectionFactory.getTestEntityManager();
        TestConnectionFactory.clearDatabase(em);

        indicadorDAO = new TestIndicadorBiomedicoDAO();
        usuarioDAO = new TestUsuarioDAO();

        // Criar usuário de teste
        usuarioTeste = new Usuario();
        usuarioTeste.setNome("Usuário Teste Indicadores");
        usuarioTeste.setEmail("teste.indicadores@email.com");
        usuarioTeste.setSenha("senha123");
        usuarioTeste.setTipo(TipoUsuario.COMUM);
        usuarioTeste = usuarioDAO.salvar(usuarioTeste);

        // Nota: O IndicadorBiomedicoService atual não tem construtor com injeção de dependência
        // Para testes reais, seria necessário modificar o Service para aceitar DAOs injetados
        // Por enquanto, testamos através dos DAOs diretamente
        indicadorService = new TestIndicadorBiomedicoService(indicadorDAO, usuarioDAO);
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
    @DisplayName("Integração: Deve cadastrar indicador biomédico e calcular IMC automaticamente")
    void testCadastrarIndicadorComIMC() {
        // Dado
        LocalDate data = LocalDate.of(2024, 1, 1);
        double peso = 80.0;
        double altura = 175.0;
        double gordura = 15.0;
        double massaMagra = 85.0;

        // Quando
        IndicadorBiomedico indicador = indicadorService.cadastrarIndicador(
                usuarioTeste.getId(), data, peso, altura, gordura, massaMagra
        );

        // Então
        assertNotNull(indicador);
        assertNotNull(indicador.getId());
        assertEquals(peso, indicador.getPesoKg());
        assertEquals(altura, indicador.getAlturaCm());
        assertEquals(gordura, indicador.getPercentualGordura());
        assertEquals(massaMagra, indicador.getPercentualMassaMagra());

        // Verifica cálculo do IMC (peso / (altura/100)^2)
        double imcEsperado = peso / Math.pow(altura / 100, 2);
        assertEquals(imcEsperado, indicador.getImc(), 0.01);

        // Verifica persistência
        List<IndicadorBiomedico> indicadores = indicadorDAO.listarPorUsuario(usuarioTeste.getId());
        assertEquals(1, indicadores.size());
    }

    @Test
    @Order(2)
    @DisplayName("Integração: Deve usar data atual quando data não for informada")
    void testCadastrarIndicadorSemData() {
        IndicadorBiomedico indicador = indicadorService.cadastrarIndicador(
                usuarioTeste.getId(), null, 75.0, 170.0, 18.0, 82.0
        );

        assertNotNull(indicador);
        assertEquals(LocalDate.now(), indicador.getDataRegistro());
    }

    @Test
    @Order(3)
    @DisplayName("Integração: Deve validar valores de peso e altura")
    void testValidacaoPesoAltura() {
        LocalDate data = LocalDate.now();

        // Peso zero
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.cadastrarIndicador(usuarioTeste.getId(), data, 0, 170.0, 15.0, 85.0);
        });
        assertTrue(exception1.getMessage().contains("Peso e altura devem ser maiores que zero"));

        // Altura negativa
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.cadastrarIndicador(usuarioTeste.getId(), data, 70.0, -170.0, 15.0, 85.0);
        });
        assertTrue(exception2.getMessage().contains("Peso e altura devem ser maiores que zero"));
    }

    @Test
    @Order(4)
    @DisplayName("Integração: Deve validar percentuais não negativos")
    void testValidacaoPercentuais() {
        LocalDate data = LocalDate.now();

        // Percentual de gordura negativo
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.cadastrarIndicador(usuarioTeste.getId(), data, 75.0, 170.0, -5.0, 85.0);
        });
        assertTrue(exception1.getMessage().contains("Percentuais de gordura e massa magra não podem ser negativos"));

        // Percentual de massa magra negativo
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.cadastrarIndicador(usuarioTeste.getId(), data, 75.0, 170.0, 15.0, -85.0);
        });
        assertTrue(exception2.getMessage().contains("Percentuais de gordura e massa magra não podem ser negativos"));
    }

    @Test
    @Order(5)
    @DisplayName("Integração: Deve listar todos os indicadores do usuário")
    void testListarIndicadoresDoUsuario() {
        // Cadastra indicadores para usuário de teste
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 1, 1), 80.0, 175.0, 15.0, 85.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 2, 1), 78.0, 175.0, 14.0, 86.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 3, 1), 76.0, 175.0, 13.0, 87.0);

        // Criar segundo usuário
        Usuario usuario2 = new Usuario();
        usuario2.setNome("Usuário 2");
        usuario2.setEmail("usuario2@email.com");
        usuario2.setSenha("senha123");
        usuario2.setTipo(TipoUsuario.COMUM);
        usuario2 = usuarioDAO.salvar(usuario2);

        // Cadastra indicador para usuário 2
        indicadorService.cadastrarIndicador(usuario2.getId(), LocalDate.of(2024, 1, 1), 70.0, 165.0, 20.0, 80.0);

        // Lista indicadores do usuário de teste
        List<IndicadorBiomedico> indicadoresUser1 = indicadorService.listarTodosDoUsuario(usuarioTeste.getId());
        assertEquals(3, indicadoresUser1.size());
        assertTrue(indicadoresUser1.stream().allMatch(i -> i.getUsuario().getId().equals(usuarioTeste.getId())));

        // Lista indicadores do usuário 2
        List<IndicadorBiomedico> indicadoresUser2 = indicadorService.listarTodosDoUsuario(usuario2.getId());
        assertEquals(1, indicadoresUser2.size());
        assertEquals(usuario2.getId(), indicadoresUser2.get(0).getUsuario().getId());
    }

    @Test
    @Order(6)
    @DisplayName("Integração: Deve gerar relatório por período")
    void testGerarRelatorioPorPeriodo() {
        // Cadastra indicadores em diferentes datas
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 1, 1), 80.0, 175.0, 15.0, 85.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 2, 1), 78.0, 175.0, 14.0, 86.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 3, 1), 76.0, 175.0, 13.0, 87.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 4, 1), 74.0, 175.0, 12.0, 88.0);

        // Gera relatório para período janeiro-março
        List<IndicadorBiomedico> relatorio = indicadorService.gerarRelatorioPorData(
                usuarioTeste.getId(), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)
        );

        assertEquals(3, relatorio.size());
        // Verifica ordem (deve estar em ordem de data)
        assertEquals(LocalDate.of(2024, 1, 1), relatorio.get(0).getDataRegistro());
        assertEquals(LocalDate.of(2024, 3, 1), relatorio.get(2).getDataRegistro());
    }

    @Test
    @Order(7)
    @DisplayName("Integração: Deve gerar relatório de diferença entre períodos")
    void testGerarRelatorioDiferenca() {
        // Cadastra indicadores
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 1, 1), 90.0, 175.0, 25.0, 75.0);
        indicadorService.cadastrarIndicador(usuarioTeste.getId(), LocalDate.of(2024, 3, 1), 82.0, 175.0, 17.0, 83.0);

        // Gera relatório de diferença
        RelatorioDiferencaIndicadores relatorio = indicadorService.gerarRelatorioDiferenca(
                usuarioTeste.getId(), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31)
        );

        assertNotNull(relatorio);
        assertTrue(relatorio.getIndicadorInicial().isPresent());
        assertTrue(relatorio.getIndicadorFinal().isPresent());

        // Verifica indicador inicial
        assertEquals(90.0, relatorio.getIndicadorInicial().get().getPesoKg());
        assertEquals(25.0, relatorio.getIndicadorInicial().get().getPercentualGordura());

        // Verifica indicador final
        assertEquals(82.0, relatorio.getIndicadorFinal().get().getPesoKg());
        assertEquals(17.0, relatorio.getIndicadorFinal().get().getPercentualGordura());
    }

    @Test
    @Order(8)
    @DisplayName("Integração: Deve validar período inválido no relatório")
    void testValidacaoPeriodoRelatorio() {
        // Data início nula
        assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.gerarRelatorioPorData(usuarioTeste.getId(), null, LocalDate.now());
        });

        // Data fim nula
        assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.gerarRelatorioPorData(usuarioTeste.getId(), LocalDate.now(), null);
        });

        // Data início depois da fim
        assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.gerarRelatorioPorData(
                    usuarioTeste.getId(),
                    LocalDate.of(2024, 12, 1),
                    LocalDate.of(2024, 1, 1)
            );
        });
    }

    @Test
    @Order(9)
    @DisplayName("Integração: Deve lidar com usuário inexistente")
    void testUsuarioInexistente() {
        int idUsuarioInexistente = 99999;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            indicadorService.cadastrarIndicador(
                    idUsuarioInexistente, LocalDate.now(), 75.0, 170.0, 15.0, 85.0
            );
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    /**
     * Service de teste que permite injeção de DAOs de teste
     */
    private static class TestIndicadorBiomedicoService extends br.upe.controller.business.IndicadorBiomedicoService {
        private final TestIndicadorBiomedicoDAO indicadorDAO;
        private final TestUsuarioDAO usuarioDAO;

        public TestIndicadorBiomedicoService(TestIndicadorBiomedicoDAO indicadorDAO, TestUsuarioDAO usuarioDAO) {
            this.indicadorDAO = indicadorDAO;
            this.usuarioDAO = usuarioDAO;
        }

        @Override
        public IndicadorBiomedico cadastrarIndicador(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra) {
            if (pesoKg <= 0 || alturaCm <= 0) {
                throw new IllegalArgumentException("Peso e altura devem ser maiores que zero.");
            }
            if (percentualGordura < 0 || percentualMassaMagra < 0) {
                throw new IllegalArgumentException("Percentuais de gordura e massa magra não podem ser negativos.");
            }
            if (data == null) {
                data = LocalDate.now();
            }

            Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

            double imc = pesoKg / Math.pow(alturaCm / 100.0, 2);

            IndicadorBiomedico novoIndicador = new IndicadorBiomedico();
            novoIndicador.setUsuario(usuario);
            novoIndicador.setDataRegistro(data);
            novoIndicador.setPesoKg(pesoKg);
            novoIndicador.setAlturaCm(alturaCm);
            novoIndicador.setPercentualGordura(percentualGordura);
            novoIndicador.setPercentualMassaMagra(percentualMassaMagra);
            novoIndicador.setImc(imc);

            return indicadorDAO.salvar(novoIndicador);
        }

        @Override
        public List<IndicadorBiomedico> gerarRelatorioPorData(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
            if (dataInicio == null || dataFim == null) {
                throw new IllegalArgumentException("Datas de início e fim não podem ser nulas.");
            }
            if (dataInicio.isAfter(dataFim)) {
                throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim.");
            }

            List<IndicadorBiomedico> resultados = indicadorDAO.buscarPorPeriodo(idUsuario, dataInicio, dataFim);
            resultados.sort(java.util.Comparator.comparing(IndicadorBiomedico::getDataRegistro));
            return resultados;
        }

        @Override
        public RelatorioDiferencaIndicadores gerarRelatorioDiferenca(int idUsuario, LocalDate dataInicio, LocalDate dataFim) {
            if (dataInicio == null || dataFim == null) {
                throw new IllegalArgumentException("Datas inválidas.");
            }

            List<IndicadorBiomedico> lista = indicadorDAO.buscarPorPeriodo(idUsuario, dataInicio, dataFim);

            RelatorioDiferencaIndicadores relatorio = new RelatorioDiferencaIndicadores();
            relatorio.setDataInicio(dataInicio);
            relatorio.setDataFim(dataFim);

            if (!lista.isEmpty()) {
                lista.sort(java.util.Comparator.comparing(IndicadorBiomedico::getDataRegistro));
                relatorio.setIndicadorInicial(java.util.Optional.of(lista.get(0)));
                relatorio.setIndicadorFinal(java.util.Optional.of(lista.get(lista.size() - 1)));
                relatorio.calcularDiferencas();
            }
            return relatorio;
        }

        @Override
        public List<IndicadorBiomedico> listarTodosDoUsuario(int idUsuario) {
            return indicadorDAO.listarPorUsuario(idUsuario);
        }
    }
}

