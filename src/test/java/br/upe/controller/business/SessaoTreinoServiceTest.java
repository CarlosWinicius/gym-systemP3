package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.*;
import br.upe.data.interfaces.*;
import br.upe.data.dao.ItemPlanoTreinoDAO;
import br.upe.data.dao.ItemSessaoTreinoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoTreinoServiceTest {

    @Mock
    private ISessaoTreinoRepository sessaoRepo;

    @Mock
    private IPlanoTreinoRepository planoRepo;

    @Mock
    private IExercicioRepository exercicioRepo;

    @Mock
    private IUsuarioRepository usuarioRepo;

    @Mock
    private ItemSessaoTreinoDAO itemSessaoRepo;

    @Mock
    private ItemPlanoTreinoDAO itemPlanoRepo;

    private SessaoTreinoService sessaoService;

    private PlanoTreino plano;
    private SessaoTreino sessao;
    private Exercicio exercicio;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        sessaoService = new SessaoTreinoService(sessaoRepo, planoRepo, exercicioRepo, usuarioRepo, itemSessaoRepo, itemPlanoRepo);

        // Criar usuário mock
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);

        // Criar plano de treino mock
        plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano 1");

        // Criar item do plano
        ItemPlanoTreino itemPlano = new ItemPlanoTreino();
        itemPlano.setId(1);
        itemPlano.setPlanoTreino(plano);
        itemPlano.setCargaKg(50);
        itemPlano.setRepeticoes(10);

        // Criar exercício mock
        exercicio = new Exercicio();
        exercicio.setId(1);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Exercicio 1");
        exercicio.setDescricao("Desc");
        exercicio.setCaminhoGif("gif");

        itemPlano.setExercicio(exercicio);
        plano.adicionarItem(itemPlano);

        // Criar sessão mock
        sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());
    }

    @Test
    @DisplayName("Deve iniciar sessão com sucesso")
    void testIniciarSessao_Success() {
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(1)).thenReturn(Optional.of(plano));

        SessaoTreino result = sessaoService.iniciarSessao(1, 1);

        assertNotNull(result);
        assertEquals(usuario, result.getUsuario());
        assertEquals(plano, result.getPlanoTreino());
        verify(sessaoRepo).salvar(any(SessaoTreino.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para usuário não encontrado")
    void testIniciarSessao_UsuarioNaoEncontrado() {
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoService.iniciarSessao(1, 1);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para plano não encontrado")
    void testIniciarSessao_PlanoNaoEncontrado() {
        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoService.iniciarSessao(1, 1);
        });

        assertEquals("Plano não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve registrar execução de exercício")
    void testRegistrarExecucao() {
        when(exercicioRepo.buscarPorId(1)).thenReturn(Optional.of(exercicio));

        sessaoService.registrarExecucao(sessao, 1, 12, 55.0);

        verify(itemSessaoRepo).salvar(any(ItemSessaoTreino.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar execução com exercício inexistente")
    void testRegistrarExecucao_ExercicioNaoEncontrado() {
        when(exercicioRepo.buscarPorId(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoService.registrarExecucao(sessao, 999, 12, 55.0);
        });

        assertTrue(exception.getMessage().contains("Exercício não encontrado"));
    }

    @Test
    @DisplayName("Deve salvar sessão com sucesso")
    void testSalvarSessao_Success() {
        sessaoService.salvarSessao(sessao);

        verify(sessaoRepo).salvar(sessao);
    }

    @Test
    @DisplayName("Deve gerar sugestões de atualização")
    void testVerificarAlteracoesEGerarSugestoes() {
        // Item planejado
        ItemPlanoTreino itemPlanejado = new ItemPlanoTreino();
        itemPlanejado.setId(1);
        itemPlanejado.setExercicio(exercicio);
        itemPlanejado.setRepeticoes(10);
        itemPlanejado.setCargaKg(50);

        // Item executado com valores maiores
        ItemSessaoTreino itemExecutado = new ItemSessaoTreino();
        itemExecutado.setId(1);
        itemExecutado.setExercicio(exercicio);
        itemExecutado.setRepeticoesRealizadas(12);
        itemExecutado.setCargaRealizada(55.0);

        when(itemPlanoRepo.listarPorPlano(1)).thenReturn(List.of(itemPlanejado));
        when(itemSessaoRepo.listarPorSessao(1)).thenReturn(List.of(itemExecutado));

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);

        assertEquals(1, sugestoes.size());
        SessaoTreinoService.SugestaoAtualizacaoPlano sugestao = sugestoes.get(0);
        assertEquals(1, sugestao.idExercicio);
        assertEquals("Exercicio 1", sugestao.nomeExercicio);
        assertEquals(10, sugestao.repPlanejadas);
        assertEquals(12, sugestao.repRealizadas);
        assertEquals(50.0, sugestao.cargaPlanejada);
        assertEquals(55.0, sugestao.cargaRealizada);
    }

    @Test
    @DisplayName("Deve aplicar atualizações no plano")
    void testAplicarAtualizacoesNoPlano() {
        ItemPlanoTreino itemPlanejado = new ItemPlanoTreino();
        itemPlanejado.setId(1);
        itemPlanejado.setExercicio(exercicio);
        itemPlanejado.setRepeticoes(10);
        itemPlanejado.setCargaKg(50);

        when(itemPlanoRepo.listarPorPlano(1)).thenReturn(List.of(itemPlanejado));

        sessaoService.aplicarAtualizacoesNoPlano(1, 1, 12, 55.0);

        assertEquals(12, itemPlanejado.getRepeticoes());
        assertEquals(55, itemPlanejado.getCargaKg());
        verify(itemPlanoRepo).editar(itemPlanejado);
    }

    @Test
    @DisplayName("Deve registrar execução com valores diferentes")
    void testRegistrarExecucao_ValoresDiferentes() {
        when(exercicioRepo.buscarPorId(1)).thenReturn(Optional.of(exercicio));

        sessaoService.registrarExecucao(sessao, 1, 15, 70.5);

        verify(itemSessaoRepo).salvar(any(ItemSessaoTreino.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia de sugestões quando não há exercícios")
    void testVerificarAlteracoesEGerarSugestoes_SemExercicios() {
        when(itemPlanoRepo.listarPorPlano(1)).thenReturn(List.of());
        when(itemSessaoRepo.listarPorSessao(1)).thenReturn(List.of());

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);

        assertTrue(sugestoes.isEmpty());
    }

    @Test
    @DisplayName("Não deve gerar sugestões quando valores são menores ou iguais")
    void testVerificarAlteracoesEGerarSugestoes_SemMelhoria() {
        ItemPlanoTreino itemPlanejado = new ItemPlanoTreino();
        itemPlanejado.setId(1);
        itemPlanejado.setExercicio(exercicio);
        itemPlanejado.setRepeticoes(10);
        itemPlanejado.setCargaKg(50);

        ItemSessaoTreino itemExecutado = new ItemSessaoTreino();
        itemExecutado.setId(1);
        itemExecutado.setExercicio(exercicio);
        itemExecutado.setRepeticoesRealizadas(8); // Menor que o planejado
        itemExecutado.setCargaRealizada(45.0); // Menor que o planejado

        when(itemPlanoRepo.listarPorPlano(1)).thenReturn(List.of(itemPlanejado));
        when(itemSessaoRepo.listarPorSessao(1)).thenReturn(List.of(itemExecutado));

        List<SessaoTreinoService.SugestaoAtualizacaoPlano> sugestoes = sessaoService.verificarAlteracoesEGerarSugestoes(sessao);

        assertTrue(sugestoes.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando plano não pertence ao usuário")
    void testIniciarSessao_PlanoNaoPertenceAoUsuario() {
        Usuario outroUsuario = new Usuario("Outro", "outro@email.com", "senha", TipoUsuario.COMUM);
        outroUsuario.setId(2);

        PlanoTreino planoOutroUsuario = new PlanoTreino();
        planoOutroUsuario.setId(2);
        planoOutroUsuario.setUsuario(outroUsuario);
        planoOutroUsuario.setNome("Plano de Outro");

        when(usuarioRepo.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoRepo.buscarPorId(2)).thenReturn(Optional.of(planoOutroUsuario));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sessaoService.iniciarSessao(1, 2);
        });

        assertEquals("Este plano não pertence a você.", exception.getMessage());
    }
}

