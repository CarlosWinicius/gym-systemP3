package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExercicioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);
    }

    @Test
    @DisplayName("Deve criar exercício com construtor vazio")
    void testCriarExercicioComConstrutorVazio() {
        Exercicio exercicio = new Exercicio();

        assertNotNull(exercicio);
        assertNull(exercicio.getId());
        assertNull(exercicio.getUsuario());
        assertNull(exercicio.getNome());
        assertNull(exercicio.getDescricao());
        assertNull(exercicio.getCaminhoGif());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        Exercicio exercicio = new Exercicio();
        exercicio.setId(10);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Supino");
        exercicio.setDescricao("Exercício para peitoral");
        exercicio.setCaminhoGif("/gifs/supino.gif");

        assertEquals(10, exercicio.getId());
        assertEquals(usuario, exercicio.getUsuario());
        assertEquals("Supino", exercicio.getNome());
        assertEquals("Exercício para peitoral", exercicio.getDescricao());
        assertEquals("/gifs/supino.gif", exercicio.getCaminhoGif());
    }

    @Test
    @DisplayName("Deve manter relacionamento com usuário")
    void testRelacionamentoComUsuario() {
        Exercicio exercicio = new Exercicio();
        exercicio.setUsuario(usuario);

        assertNotNull(exercicio.getUsuario());
        assertEquals(1, exercicio.getUsuario().getId());
        assertEquals("Test User", exercicio.getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve gerar toString sem erro")
    void testToString() {
        Exercicio exercicio = new Exercicio();
        exercicio.setId(1);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Agachamento");
        exercicio.setDescricao("Exercício para pernas");
        exercicio.setCaminhoGif("/gifs/agachamento.gif");

        String resultado = exercicio.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Exercicio"));
    }

    @Test
    @DisplayName("Deve permitir alterar nome do exercício")
    void testAlterarNome() {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Nome Original");

        assertEquals("Nome Original", exercicio.getNome());

        exercicio.setNome("Nome Atualizado");

        assertEquals("Nome Atualizado", exercicio.getNome());
    }

    @Test
    @DisplayName("Deve permitir alterar descrição do exercício")
    void testAlterarDescricao() {
        Exercicio exercicio = new Exercicio();
        exercicio.setDescricao("Descrição Original");

        assertEquals("Descrição Original", exercicio.getDescricao());

        exercicio.setDescricao("Descrição Atualizada");

        assertEquals("Descrição Atualizada", exercicio.getDescricao());
    }
}

