package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertNull;

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
        assert exercicioCompleto.getIdExercicio() == 1;
        assert exercicioCompleto.getIdUsuario() == 100;
        assert exercicioCompleto.getNome().equals("Flexão");
        assert exercicioCompleto.getDescricao().equals("Exercício para peitoral");
        assert exercicioCompleto.getCaminhoGif().equals("flexao.gif");
    }

    @Test
    @DisplayName("Teste do construtor sem ID")
    void testConstrutorSemId() {
        assert exercicioSemId.getIdExercicio() == 0; // ID padrão é 0
        assert exercicioSemId.getIdUsuario() == 100;
        assert exercicioSemId.getNome().equals("Agachamento");
        assert exercicioSemId.getDescricao().equals("Exercício para pernas");
        assert exercicioSemId.getCaminhoGif().equals("agachamento.gif");
    }

    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID exercício
        exercicioCompleto.setIdExercicio(2);
        assert exercicioCompleto.getIdExercicio() == 2;
        //ID usuário
        exercicioCompleto.setIdUsuario(200);
        assert exercicioCompleto.getIdUsuario() == 200;
        //nome do exercício
        exercicioCompleto.setNome("Supino");
        assert exercicioCompleto.getNome().equals("Supino");
        //descrição do exercício
        exercicioCompleto.setDescricao("Exercício para peito");
        assert exercicioCompleto.getDescricao().equals("Exercício para peito");
        //caminho do gif
        exercicioCompleto.setCaminhoGif("supino.gif");
        assert exercicioCompleto.getCaminhoGif().equals("supino.gif");
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
        assert exercicioCompleto.toString().equals(resultado);
    }

    @Test
    @DisplayName("Teste do método toString do exercício sem ID")
    void testToStringSemId() {
        //(ID deve ser 0)
        String resultadosemID = "ID: 0 | Nome: Agachamento | Descrição: Exercício para pernas | GIF: agachamento.gif";
        assert exercicioSemId.toString().equals(resultadosemID);
    }

    //testar valores limites
    @Test
    @DisplayName("Deve aceitar valores limites para IDs")
    void testValoresLimites() {
        //valores maximos
        exercicioCompleto.setIdExercicio(Integer.MAX_VALUE);
        exercicioCompleto.setIdUsuario(Integer.MAX_VALUE);
        assert exercicioCompleto.getIdExercicio() == Integer.MAX_VALUE;
        assert exercicioCompleto.getIdUsuario() == Integer.MAX_VALUE;
        //valores minimos
        exercicioCompleto.setIdExercicio(Integer.MIN_VALUE);
        exercicioCompleto.setIdUsuario(Integer.MIN_VALUE);
        assert exercicioCompleto.getIdExercicio() == Integer.MIN_VALUE;
        assert exercicioCompleto.getIdUsuario() == Integer.MIN_VALUE;
    }
    @Test
    @DisplayName("Deve aceitar strings vazias")
    void testStringsVazias() {
        exercicioCompleto.setNome("");
        exercicioCompleto.setDescricao("");
        exercicioCompleto.setCaminhoGif("");

        assert exercicioCompleto.getNome().isEmpty();
        assert exercicioCompleto.getDescricao().isEmpty();
        assert exercicioCompleto.getCaminhoGif().isEmpty();
    }

    //testar comportamento com caracteres especiais
    @Test
    @DisplayName("Deve aceitar caracteres especiais")
    void testCaracteresEspeciais() {
        String nomeEspecial = "Exercício com acentuação á é í ó ú ã õ ç";
        //atribuindo valor com caracteres especiais
        exercicioCompleto.setNome(nomeEspecial);
        //verificando se o valor foi atribuído corretamente
        assert exercicioCompleto.getNome().equals(nomeEspecial);
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
        assert exercicioCompleto.getDescricao().equals(stringLonga);
    }

}