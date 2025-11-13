package br.upe.controller.business;

import br.upe.data.beans.Exercicio;
import br.upe.data.repository.IExercicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExercicioServiceTest {

    @Mock
    private IExercicioRepository exercicioRepository;

    @InjectMocks
    private ExercicioService exercicioService;

    private Exercicio exercicio;

    @BeforeEach
    void setUp() {
        exercicio = new Exercicio(1, 1, "Exercicio 1", "Descricao", "caminho.gif");
    }

    @Test
    @DisplayName("Deve cadastrar exercício com sucesso")
    void testCadastrarExercicio_Success() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of());
        when(exercicioRepository.salvar(any(Exercicio.class))).thenReturn(exercicio);

        Exercicio result = exercicioService.cadastrarExercicio(1, "Exercicio 1", "Descricao", "caminho.gif");

        assertNotNull(result);
        assertEquals("Exercicio 1", result.getNome());
        verify(exercicioRepository).salvar(any(Exercicio.class));
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio")
    void testCadastrarExercicio_NomeVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(1, "", "Descricao", "caminho.gif");
        });

        assertEquals("Nome do exercício não pode ser vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome já existente")
    void testCadastrarExercicio_NomeJaExiste() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of(exercicio));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.cadastrarExercicio(1, "Exercicio 1", "Descricao", "caminho.gif");
        });

        assertEquals("Você já possui um exercício com o nome 'Exercicio 1'.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve listar exercícios do usuário")
    void testListarExerciciosDoUsuario() {
        List<Exercicio> exercicios = List.of(exercicio);
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(exercicios);

        List<Exercicio> result = exercicioService.listarExerciciosDoUsuario(1);

        assertEquals(1, result.size());
        assertEquals(exercicios, result);
    }

    @Test
    @DisplayName("Deve buscar exercício por nome")
    void testBuscarExercicioDoUsuarioPorNome() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of(exercicio));

        Optional<Exercicio> result = exercicioService.buscarExercicioDoUsuarioPorNome(1, "Exercicio 1");

        assertTrue(result.isPresent());
        assertEquals(exercicio, result.get());
    }

    @Test
    @DisplayName("Deve retornar vazio para nome vazio")
    void testBuscarExercicioDoUsuarioPorNome_NomeVazio() {
        Optional<Exercicio> result = exercicioService.buscarExercicioDoUsuarioPorNome(1, "");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve buscar exercício por ID global")
    void testBuscarExercicioPorIdGlobal() {
        when(exercicioRepository.buscarPorId(1)).thenReturn(Optional.of(exercicio));

        Optional<Exercicio> result = exercicioService.buscarExercicioPorIdGlobal(1);

        assertTrue(result.isPresent());
        assertEquals(exercicio, result.get());
    }

    @Test
    @DisplayName("Deve deletar exercício por nome com sucesso")
    void testDeletarExercicioPorNome_Success() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of(exercicio));

        boolean result = exercicioService.deletarExercicioPorNome(1, "Exercicio 1");

        assertTrue(result);
        verify(exercicioRepository).deletar(1);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar exercício inexistente")
    void testDeletarExercicioPorNome_Inexistente() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of());

        boolean result = exercicioService.deletarExercicioPorNome(1, "Exercicio Inexistente");

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio na deleção")
    void testDeletarExercicioPorNome_NomeVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.deletarExercicioPorNome(1, "");
        });

        assertEquals("Nome do exercício para deletar não pode ser vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar exercício com sucesso")
    void testAtualizarExercicio_Success() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of(exercicio));

        exercicioService.atualizarExercicio(1, "Exercicio 1", "Novo Nome", "Nova Descricao", "novo.gif");

        assertEquals("Novo Nome", exercicio.getNome());
        assertEquals("Nova Descricao", exercicio.getDescricao());
        assertEquals("novo.gif", exercicio.getCaminhoGif());
        verify(exercicioRepository).editar(exercicio);
    }

    @Test
    @DisplayName("Deve lançar exceção para nome atual vazio")
    void testAtualizarExercicio_NomeAtualVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "", "Novo Nome", "Descricao", "gif");
        });

        assertEquals("O nome atual do exercício não pode ser vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para exercício não encontrado na atualização")
    void testAtualizarExercicio_NaoEncontrado() {
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "Exercicio 1", "Novo Nome", "Descricao", "gif");
        });

        assertEquals("Erro: Exercício 'Exercicio 1' não encontrado entre os seus exercícios para atualização.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para novo nome já existente")
    void testAtualizarExercicio_NovoNomeJaExiste() {
        Exercicio outroExercicio = new Exercicio(2, 1, "Outro", "Desc", "gif");
        when(exercicioRepository.buscarTodosDoUsuario(1)).thenReturn(List.of(exercicio, outroExercicio));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exercicioService.atualizarExercicio(1, "Exercicio 1", "Outro", "Descricao", "gif");
        });

        assertEquals("Você já possui um exercício com o novo nome 'Outro'.", exception.getMessage());
    }
}