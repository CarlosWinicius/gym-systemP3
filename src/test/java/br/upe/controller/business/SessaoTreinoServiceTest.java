//package br.upe.controller.business;
//
//import br.upe.data.beans.*;
//import br.upe.data.repository.IExercicioRepository;
//import br.upe.data.repository.IPlanoTreinoRepository;
//import br.upe.data.repository.ISessaoTreinoRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SessaoTreinoServiceTest {
//
//    @Mock
//    private ISessaoTreinoRepository sessaoRepo;
//
//    @Mock
//    private IPlanoTreinoRepository planoRepo;
//
//    @Mock
//    private IExercicioRepository exercicioRepo;
//
//    @InjectMocks
//    private SessaoTreinoService sessaoService;
//
//    private PlanoTreino plano;
//    private SessaoTreino sessao;
//    private Exercicio exercicio;
//
//    @BeforeEach
//    void setUp() {
//        plano = new PlanoTreino(1, "Plano 1");
//        ItemPlanoTreino itemPlano = new ItemPlanoTreino(1, 50, 10);
//        plano.adicionarItem(itemPlano);
//        sessao = new SessaoTreino(1, 1);
//        exercicio = new Exercicio(1, 1, "Exercicio 1", "Desc", "gif");
//    }
//
//    @Test
//    @DisplayName("Deve iniciar sessão com sucesso")
//    void testIniciarSessao_Success() {
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(plano));
//
//        SessaoTreino result = sessaoService.iniciarSessao(1, 1);
//
//        assertNotNull(result);
//        assertEquals(1, result.getIdUsuario());
//        assertEquals(1, result.getIdPlanoTreino());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para plano não encontrado")
//    void testIniciarSessao_PlanoNaoEncontrado() {
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.empty());
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            sessaoService.iniciarSessao(1, 1);
//        });
//
//        assertEquals("Plano de treino com ID 1 não encontrado ou não pertence a você.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve registrar execução de exercício")
//    void testRegistrarExecucao() {
//        sessaoService.registrarExecucao(sessao, 1, 12, 55.0);
//
//        assertEquals(1, sessao.getItensExecutados().size());
//        ItemSessaoTreino item = sessao.getItensExecutados().get(0);
//        assertEquals(1, item.getIdExercicio());
//        assertEquals(12, item.getRepeticoesRealizadas());
//        assertEquals(55.0, item.getCargaRealizada());
//    }
//
//    @Test
//    @DisplayName("Deve salvar sessão com sucesso")
//    void testSalvarSessao_Success() {
//        ItemSessaoTreino item = new ItemSessaoTreino(1, 10, 50.0);
//        sessao.adicionarItemExecutado(item);
//
//        sessaoService.salvarSessao(sessao);
//
//        verify(sessaoRepo).salvar(sessao);
//    }
//
//    @Test
//    @DisplayName("Não deve salvar sessão vazia")
//    void testSalvarSessao_Vazia() {
//        sessaoService.salvarSessao(sessao);
//
//        verify(sessaoRepo, never()).salvar(any(SessaoTreino.class));
//    }
//
//    @Test
//    @DisplayName("Deve gerar sugestões de atualização")
//    void testVerificarAlteracoesEGerarSugestoes() {
//        ItemSessaoTreino itemExecutado = new ItemSessaoTreino(1, 12, 55.0);
//        sessao.adicionarItemExecutado(itemExecutado);
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(plano));
//        when(exercicioRepo.buscarPorId(1)).thenReturn(Optional.of(exercicio));
//
//        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);
//
//        assertEquals(1, sugestoes.size());
//        SessaoTreinoService.SugestaoAtualizacaoPlano sugestao = sugestoes.get(0);
//        assertEquals(1, sugestao.idExercicio);
//        assertEquals("Exercicio 1", sugestao.nomeExercicio);
//        assertEquals(10, sugestao.repPlanejadas);
//        assertEquals(12, sugestao.repRealizadas);
//        assertEquals(50.0, sugestao.cargaPlanejada);
//        assertEquals(55.0, sugestao.cargaRealizada);
//    }
//
//    @Test
//    @DisplayName("Deve aplicar atualizações no plano")
//    void testAplicarAtualizacoesNoPlano() {
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(plano));
//
//        sessaoService.aplicarAtualizacoesNoPlano(1, 1, 12, 55.0);
//
//        ItemPlanoTreino item = plano.getItensTreino().get(0);
//        assertEquals(12, item.getRepeticoes());
//        assertEquals(55, item.getCargaKg());
//        verify(planoRepo).editar(plano);
//    }
//
//    @Test
//    @DisplayName("Deve registrar execução com valores diferentes")
//    void testRegistrarExecucao_ValoresDiferentes() {
//        sessaoService.registrarExecucao(sessao, 2, 15, 70.5);
//
//        assertEquals(1, sessao.getItensExecutados().size());
//        ItemSessaoTreino item = sessao.getItensExecutados().get(0);
//        assertEquals(2, item.getIdExercicio());
//        assertEquals(15, item.getRepeticoesRealizadas());
//        assertEquals(70.5, item.getCargaRealizada());
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista vazia de sugestões quando não há exercícios")
//    void testVerificarAlteracoesEGerarSugestoes_SemExercicios() {
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(plano));
//
//        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);
//
//        assertTrue(sugestoes.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Não deve gerar sugestões quando plano não existe")
//    void testVerificarAlteracoesEGerarSugestoes_PlanoInexistente() {
//        ItemSessaoTreino itemExecutado = new ItemSessaoTreino(1, 12, 55.0);
//        sessao.adicionarItemExecutado(itemExecutado);
//        when(planoRepo.buscarPorId(1)).thenReturn(Optional.empty());
//
//        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);
//
//        assertTrue(sugestoes.isEmpty());
//    }
//}
