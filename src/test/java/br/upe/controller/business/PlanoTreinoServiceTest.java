//package br.upe.controller.business;
//
//import br.upe.data.beans.Exercicio;
//import br.upe.data.beans.ItemPlanoTreino;
//import br.upe.data.beans.PlanoTreino;
//import br.upe.data.repository.IExercicioRepository;
//import br.upe.data.repository.IPlanoTreinoRepository;
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
//class PlanoTreinoServiceTest {
//
//    @Mock
//    private IPlanoTreinoRepository planoTreinoRepository;
//
//    @Mock
//    private IExercicioRepository exercicioRepository;
//
//    @InjectMocks
//    private PlanoTreinoService planoTreinoService;
//
//    private PlanoTreino plano;
//    private Exercicio exercicio;
//
//    @BeforeEach
//    void setUp() {
//        plano = new PlanoTreino(1, "Plano 1");
//        plano.setIdPlano(1);
//        exercicio = new Exercicio(1, 1, "Exercicio 1", "Desc", "gif");
//    }
//
//    @Test
//    @DisplayName("Deve criar plano com sucesso")
//    void testCriarPlano_Success() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.empty());
//        when(planoTreinoRepository.salvar(any(PlanoTreino.class))).thenReturn(plano);
//
//        PlanoTreino result = planoTreinoService.criarPlano(1, "Plano 1");
//
//        assertNotNull(result);
//        assertEquals("Plano 1", result.getNome());
//        verify(planoTreinoRepository).salvar(any(PlanoTreino.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para nome vazio")
//    void testCriarPlano_NomeVazio() {
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.criarPlano(1, "");
//        });
//
//        assertEquals("Nome do plano não pode ser vazio.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para nome já existente")
//    void testCriarPlano_NomeJaExiste() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.criarPlano(1, "Plano 1");
//        });
//
//        assertEquals("Você já possui um plano com o nome 'Plano 1'.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve adicionar exercício ao plano com sucesso")
//    void testAdicionarExercicioAoPlano_Success() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
//
//        planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);
//
//        assertEquals(1, plano.getItensTreino().size());
//        verify(planoTreinoRepository).editar(plano);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para plano não encontrado")
//    void testAdicionarExercicioAoPlano_PlanoNaoEncontrado() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.empty());
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);
//        });
//
//        assertEquals("Plano 'Plano 1' não encontrado ou não pertence a você.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção para exercício não encontrado")
//    void testAdicionarExercicioAoPlano_ExercicioNaoEncontrado() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.empty());
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 50, 10);
//        });
//
//        assertEquals("Exercício com ID 1 não encontrado ou não pertence a você.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve remover exercício do plano com sucesso")
//    void testRemoverExercicioDoPlano_Success() {
//        ItemPlanoTreino item = new ItemPlanoTreino(1, 50, 10);
//        plano.adicionarItem(item);
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        planoTreinoService.removerExercicioDoPlano(1, "Plano 1", 1);
//
//        assertEquals(0, plano.getItensTreino().size());
//        verify(planoTreinoRepository).editar(plano);
//    }
//
//    @Test
//    @DisplayName("Deve listar planos do usuário")
//    void testListarMeusPlanos() {
//        List<PlanoTreino> planos = List.of(plano);
//        when(planoTreinoRepository.buscarTodosDoUsuario(1)).thenReturn(planos);
//
//        List<PlanoTreino> result = planoTreinoService.listarMeusPlanos(1);
//
//        assertEquals(1, result.size());
//        assertEquals(planos, result);
//    }
//
//    @Test
//    @DisplayName("Deve buscar plano por nome e usuário")
//    void testBuscarPlanoPorNomeEUsuario() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        Optional<PlanoTreino> result = planoTreinoService.buscarPlanoPorNomeEUsuario(1, "Plano 1");
//
//        assertTrue(result.isPresent());
//        assertEquals(plano, result.get());
//    }
//
//    @Test
//    @DisplayName("Deve editar plano com sucesso")
//    void testEditarPlano_Success() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        planoTreinoService.editarPlano(1, "Plano 1", "Novo Plano");
//
//        assertEquals("Novo Plano", plano.getNome());
//        verify(planoTreinoRepository).editar(plano);
//    }
//
//    @Test
//    @DisplayName("Deve deletar plano com sucesso")
//    void testDeletarPlano_Success() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        boolean result = planoTreinoService.deletarPlano(1, "Plano 1");
//
//        assertTrue(result);
//        verify(planoTreinoRepository).deletar(1);
//    }
//
//    @Test
//    @DisplayName("Deve retornar false ao tentar deletar plano inexistente")
//    void testDeletarPlano_Inexistente() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.empty());
//
//        boolean result = planoTreinoService.deletarPlano(1, "Plano 1");
//
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção ao tentar editar plano inexistente")
//    void testEditarPlano_PlanoNaoEncontrado() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.empty());
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.editarPlano(1, "Plano 1", "Novo Nome");
//        });
//
//        assertEquals("Plano 'Plano 1' não encontrado ou não pertence a você.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção ao tentar remover exercício de plano inexistente")
//    void testRemoverExercicioDoPlano_PlanoNaoEncontrado() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.empty());
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            planoTreinoService.removerExercicioDoPlano(1, "Plano 1", 1);
//        });
//
//        assertEquals("Plano 'Plano 1' não encontrado ou não pertence a você.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Não deve alterar nome ao editar com nome vazio")
//    void testEditarPlano_NovoNomeVazio() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//
//        planoTreinoService.editarPlano(1, "Plano 1", "");
//
//        assertEquals("Plano 1", plano.getNome());
//        verify(planoTreinoRepository).editar(plano);
//    }
//
//    @Test
//    @DisplayName("Deve adicionar exercício ao plano com valores válidos")
//    void testAdicionarExercicioAoPlano_ValoresValidos() {
//        when(planoTreinoRepository.buscarPorNomeEUsuario(1, "Plano 1")).thenReturn(Optional.of(plano));
//        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));
//
//        planoTreinoService.adicionarExercicioAoPlano(1, "Plano 1", 1, 100, 20);
//
//        verify(planoTreinoRepository).editar(plano);
//        assertTrue(plano.getItensTreino().size() > 0);
//    }
//}