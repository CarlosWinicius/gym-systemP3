package br.upe.controller.business;

import br.upe.data.TipoUsuario;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import br.upe.data.entities.Usuario;
import br.upe.data.interfaces.IExercicioRepository;
import br.upe.data.interfaces.IPlanoTreinoRepository;
import br.upe.data.interfaces.IUsuarioRepository;
import br.upe.data.dao.ItemPlanoTreinoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoServiceTest {

    @Mock
    private IPlanoTreinoRepository planoTreinoRepository;

    @Mock
    private IExercicioRepository exercicioRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private ItemPlanoTreinoDAO itemDAO;

    private PlanoTreinoService planoTreinoService;

    private PlanoTreino plano;
    private Exercicio exercicio;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Criar instância manualmente com os mocks
        planoTreinoService = new PlanoTreinoService(planoTreinoRepository, exercicioRepository, usuarioRepository, itemDAO);

        // Criar usuário mock
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);

        // Criar plano de treino mock
        plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano 1");

        // Criar exercício mock
        exercicio = new Exercicio();
        exercicio.setId(1);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Exercicio 1");
        exercicio.setDescricao("Desc");
        exercicio.setCaminhoGif("gif");
    }

    @Test
    @DisplayName("Deve criar plano com sucesso")
    void testCriarPlano_Success() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.of(usuario));
        when(planoTreinoRepository.salvar(any(PlanoTreino.class))).thenReturn(plano);

        PlanoTreino result = planoTreinoService.criarPlano(1, "Plano 1");

        assertNotNull(result);
        assertEquals("Plano 1", result.getNome());
        verify(planoTreinoRepository).salvar(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio")
    void testCriarPlano_NomeVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.criarPlano(1, "");
        });

        assertEquals("Nome do plano não pode ser vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome já existente")
    void testCriarPlano_NomeJaExiste() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.criarPlano(1, "Plano 1");
        });

        assertEquals("Você já possui um plano com o nome 'Plano 1'.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar exercício ao plano com sucesso")
    void testAdicionarExercicioAoPlano_Success() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
        when(itemDAO.listarPorPlano(1)).thenReturn(new ArrayList<>());

        planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);

        verify(itemDAO).salvar(any(ItemPlanoTreino.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para plano não encontrado")
    void testAdicionarExercicioAoPlano_PlanoNaoEncontrado() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);
        });

        assertEquals("Plano 'Plano 1' não encontrado ou não pertence a você.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para exercício não encontrado")
    void testAdicionarExercicioAoPlano_ExercicioNaoEncontrado() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);
        });

        assertEquals("Exercício com ID 1 não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover exercício do plano com sucesso")
    void testRemoverExercicioDoPlano_Success() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setId(1);
        item.setPlanoTreino(plano);
        item.setExercicio(exercicio);
        item.setCargaKg(50);
        item.setRepeticoes(10);

        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));
        when(itemDAO.listarPorPlano(1)).thenReturn(List.of(item));

        planoTreinoService.removerExercicioDoPlano(1, "Plano 1", 1);

        verify(itemDAO).deletar(1);
    }

    @Test
    @DisplayName("Deve listar planos do usuário")
    void testListarMeusPlanos() {
        List<PlanoTreino> planos = List.of(plano);
        when(planoTreinoRepository.listarTodos()).thenReturn(planos);

        List<PlanoTreino> result = planoTreinoService.listarMeusPlanos(1);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Deve buscar plano por nome e usuário")
    void testBuscarPlanoPorNomeEUsuario() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));

        Optional<PlanoTreino> result = planoTreinoService.buscarPlanoPorNomeEUsuario(1, "Plano 1");

        assertTrue(result.isPresent());
        assertEquals(plano, result.get());
    }

    @Test
    @DisplayName("Deve editar plano com sucesso")
    void testEditarPlano_Success() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));

        planoTreinoService.editarPlano(1, "Plano 1", "Novo Plano");

        assertEquals("Novo Plano", plano.getNome());
        verify(planoTreinoRepository).editar(plano);
    }

    @Test
    @DisplayName("Deve deletar plano com sucesso")
    void testDeletarPlano_Success() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));

        boolean result = planoTreinoService.deletarPlano(1, "Plano 1");

        assertTrue(result);
        verify(planoTreinoRepository).deletar(1);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar plano inexistente")
    void testDeletarPlano_Inexistente() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());

        boolean result = planoTreinoService.deletarPlano(1, "Plano 1");

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar editar plano inexistente")
    void testEditarPlano_PlanoNaoEncontrado() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.editarPlano(1, "Plano 1", "Novo Nome");
        });

        assertEquals("Plano 'Plano 1' não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover exercício de plano inexistente")
    void testRemoverExercicioDoPlano_PlanoNaoEncontrado() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.removerExercicioDoPlano(1, "Plano 1", 1);
        });

        assertEquals("Plano 'Plano 1' não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve alterar nome ao editar com nome igual")
    void testEditarPlano_MesmoNome() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));

        planoTreinoService.editarPlano(1, "Plano 1", "Plano 1");

        assertEquals("Plano 1", plano.getNome());
        verify(planoTreinoRepository, never()).editar(any(PlanoTreino.class));
    }

    @Test
    @DisplayName("Deve adicionar exercício ao plano com valores válidos")
    void testAdicionarExercicioAoPlano_ValoresValidos() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of(plano));
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
        when(itemDAO.listarPorPlano(1)).thenReturn(new ArrayList<>());

        planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 100, 20);

        verify(itemDAO).salvar(any(ItemPlanoTreino.class));
    }

    @Test
    @DisplayName("Deve retornar vazio para busca com nome nulo")
    void testBuscarPlanoPorNomeEUsuario_NomeNulo() {
        Optional<PlanoTreino> result = planoTreinoService.buscarPlanoPorNomeEUsuario(1, null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao criar plano")
    void testCriarPlano_UsuarioNaoEncontrado() {
        when(planoTreinoRepository.listarTodos()).thenReturn(List.of());
        when(usuarioRepository.buscarPorId(1)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            planoTreinoService.criarPlano(1, "Novo Plano");
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }
}
