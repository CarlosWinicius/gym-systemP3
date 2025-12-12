package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPlanoTreinoTest {

    private PlanoTreino plano;
    private Exercicio exercicio;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);

        plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano A");

        exercicio = new Exercicio();
        exercicio.setId(1);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Supino");
        exercicio.setDescricao("Exercício para peitoral");
        exercicio.setCaminhoGif("/gifs/supino.gif");
    }

    @Test
    @DisplayName("Deve criar item com construtor vazio")
    void testCriarItemComConstrutorVazio() {
        ItemPlanoTreino item = new ItemPlanoTreino();

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getPlanoTreino());
        assertNull(item.getExercicio());
        assertNull(item.getCargaKg());
        assertNull(item.getRepeticoes());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        ItemPlanoTreino item = new ItemPlanoTreino();

        item.setId(10);
        item.setPlanoTreino(plano);
        item.setExercicio(exercicio);
        item.setCargaKg(50);
        item.setRepeticoes(12);

        assertEquals(10, item.getId());
        assertEquals(plano, item.getPlanoTreino());
        assertEquals(exercicio, item.getExercicio());
        assertEquals(50, item.getCargaKg());
        assertEquals(12, item.getRepeticoes());
    }

    @Test
    @DisplayName("Deve manter relacionamento com plano de treino")
    void testRelacionamentoComPlano() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setPlanoTreino(plano);

        assertNotNull(item.getPlanoTreino());
        assertEquals(1, item.getPlanoTreino().getId());
        assertEquals("Plano A", item.getPlanoTreino().getNome());
    }

    @Test
    @DisplayName("Deve manter relacionamento com exercício")
    void testRelacionamentoComExercicio() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);

        assertNotNull(item.getExercicio());
        assertEquals(1, item.getExercicio().getId());
        assertEquals("Supino", item.getExercicio().getNome());
    }

    @Test
    @DisplayName("Deve permitir diferentes valores de carga")
    void testDiferentesValoresCarga() {
        ItemPlanoTreino item = new ItemPlanoTreino();

        item.setCargaKg(20);
        assertEquals(20, item.getCargaKg());

        item.setCargaKg(100);
        assertEquals(100, item.getCargaKg());
    }

    @Test
    @DisplayName("Deve permitir diferentes valores de repetições")
    void testDiferentesValoresRepeticoes() {
        ItemPlanoTreino item = new ItemPlanoTreino();

        item.setRepeticoes(8);
        assertEquals(8, item.getRepeticoes());

        item.setRepeticoes(15);
        assertEquals(15, item.getRepeticoes());
    }

    @Test
    @DisplayName("Deve gerar toString com informações do item")
    void testToString() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);
        item.setCargaKg(50);
        item.setRepeticoes(12);

        String resultado = item.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Exercício"));
        assertTrue(resultado.contains("Supino"));
        assertTrue(resultado.contains("50"));
        assertTrue(resultado.contains("12"));
    }

    @Test
    @DisplayName("Deve gerar toString mesmo sem exercício definido")
    void testToStringSemExercicio() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setCargaKg(50);
        item.setRepeticoes(12);

        String resultado = item.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Desconhecido"));
    }

    @Test
    @DisplayName("Deve permitir criar item com valores máximos")
    void testValoresMaximos() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setCargaKg(200);
        item.setRepeticoes(50);

        assertEquals(200, item.getCargaKg());
        assertEquals(50, item.getRepeticoes());
    }

    @Test
    @DisplayName("Deve permitir criar item com valores mínimos")
    void testValoresMinimos() {
        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setCargaKg(1);
        item.setRepeticoes(1);

        assertEquals(1, item.getCargaKg());
        assertEquals(1, item.getRepeticoes());
    }
}

