package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExercicioTest {

    @InjectMocks
    private Exercicio exercicioCompleto;
    private Exercicio exercicioSemId;
    private Exercicio exercicioNull;

    @BeforeEach
    void setUp() {
        //criando objetos de Exercicio para os testes
        exercicioCompleto = new Exercicio(1, 100, "Flexão", "Exercício para peitoral", "flexao.gif");
        exercicioSemId = new Exercicio(100, "Agachamento", "Exercício para pernas", "agachamento.gif");
        exercicioNull = new Exercicio(1, 100, null, null, null);
    }

    //testar construtores
    @Test
    @DisplayName("Teste do construtor completo")
    void testConstrutorCompleto() {
        assertEquals(1, exercicioCompleto.getIdExercicio());
        assertEquals(100, exercicioCompleto.getIdUsuario());
        assertEquals("Flexão", exercicioCompleto.getNome());
        assertEquals("Exercício para peitoral", exercicioCompleto.getDescricao());
        assertEquals("flexao.gif", exercicioCompleto.getCaminhoGif());
    }

    @Test
    @DisplayName("Teste do construtor sem ID")
    void testConstrutorSemId() {
        assertEquals(0, exercicioSemId.getIdExercicio()); // ID padrão é 0
        assertEquals(100, exercicioSemId.getIdUsuario());
        assertEquals("Agachamento", exercicioSemId.getNome());
        assertEquals("Exercício para pernas", exercicioSemId.getDescricao());
        assertEquals("agachamento.gif", exercicioSemId.getCaminhoGif());
    }

    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID exercício
        exercicioCompleto.setIdExercicio(2);
        assertEquals(2, exercicioCompleto.getIdExercicio());
        //ID usuário
        exercicioCompleto.setIdUsuario(200);
        assertEquals(200, exercicioCompleto.getIdUsuario());
        //nome do exercício
        exercicioCompleto.setNome("Supino");
        assertEquals("Supino", exercicioCompleto.getNome());
        //descrição do exercício
        exercicioCompleto.setDescricao("Exercício para peito");
        assertEquals("Exercício para peito", exercicioCompleto.getDescricao());
        //caminho do gif
        exercicioCompleto.setCaminhoGif("supino.gif");
        assertEquals("supino.gif", exercicioCompleto.getCaminhoGif());
    }

    //testar valores nulos
    @Test
    @DisplayName("Teste de valores nulos")
    void testValoresInvalidos() {
        //verifica se os valores nulos foram atribuídos corretamente
        assertNull(exercicioNull.getNome());
        assertNull(exercicioNull.getDescricao());
        assertNull(exercicioNull.getCaminhoGif());
        //atribuindo valores nulos via setters
        exercicioNull.setNome(null);
        exercicioNull.setDescricao(null);
        exercicioNull.setCaminhoGif(null);
        //verifica se os valores nulos foram mantidos
        assertNull(exercicioNull.getNome());
        assertNull(exercicioNull.getDescricao());
        assertNull(exercicioNull.getCaminhoGif());
    }

    //testar o toString
    @Test
    @DisplayName("Teste do método toString do exercício completo")
    void testToString() {
        String resultado = "ID: 1 | Nome: Flexão | Descrição: Exercício para peitoral | GIF: flexao.gif";
        assertEquals(resultado, exercicioCompleto.toString());
    }

    @Test
    @DisplayName("Teste do método toString do exercício sem ID")
    void testToStringSemId() {
        //(ID deve ser 0)
        String resultadosemID = "ID: 0 | Nome: Agachamento | Descrição: Exercício para pernas | GIF: agachamento.gif";
        assertEquals(resultadosemID, exercicioSemId.toString());
    }

    //testar valores limites
    @Test
    @DisplayName("Deve aceitar valores limites para IDs")
    void testValoresLimites() {
        //valores maximos
        exercicioCompleto.setIdExercicio(Integer.MAX_VALUE);
        exercicioCompleto.setIdUsuario(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, exercicioCompleto.getIdExercicio());
        assertEquals(Integer.MAX_VALUE, exercicioCompleto.getIdUsuario());
        //valores minimos
        exercicioCompleto.setIdExercicio(Integer.MIN_VALUE);
        exercicioCompleto.setIdUsuario(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, exercicioCompleto.getIdExercicio());
        assertEquals(Integer.MIN_VALUE, exercicioCompleto.getIdUsuario());
    }
    @Test
    @DisplayName("Deve aceitar strings vazias")
    void testStringsVazias() {
        exercicioCompleto.setNome("");
        exercicioCompleto.setDescricao("");
        exercicioCompleto.setCaminhoGif("");

        assertTrue(exercicioCompleto.getNome().isEmpty());
        assertTrue(exercicioCompleto.getDescricao().isEmpty());
        assertTrue(exercicioCompleto.getCaminhoGif().isEmpty());
    }

    //testar comportamento com caracteres especiais
    @Test
    @DisplayName("Deve aceitar caracteres especiais")
    void testCaracteresEspeciais() {
        String nomeEspecial = "Exercício com acentuação á é í ó ú ã õ ç";
        //atribuindo valor com caracteres especiais
        exercicioCompleto.setNome(nomeEspecial);
        //verificando se o valor foi atribuído corretamente
        assertEquals(nomeEspecial, exercicioCompleto.getNome());
    }

    //testar comportamento com strings muito longas
    @Test
    @DisplayName("Deve aceitar strings muito longas")
    void testStringsMuitoLongas() {
        //criando string muito longa
        String stringLonga = "A".repeat(1000);

        //atribuindo string muito longa
        exercicioCompleto.setDescricao(stringLonga);
        //verificando se a string foi atribuída corretamente
        assertEquals(stringLonga, exercicioCompleto.getDescricao());
    }

}