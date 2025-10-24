package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemSessaoTreinoTest {
    @InjectMocks
    private ItemSessaoTreino itemSessaoTreino;
    private ItemSessaoTreino itemSessaonulo;

    @BeforeEach
    void setUp() {
        itemSessaoTreino = new ItemSessaoTreino(1, 10, 50.0);
        itemSessaonulo = new ItemSessaoTreino(0, 0, 0.0);
    }

    //testar construtor
    @Test
    @DisplayName("Teste do construtor")
    void testConstrutor() {
        assertEquals(1, itemSessaoTreino.getIdExercicio());
        assertEquals(10, itemSessaoTreino.getRepeticoesRealizadas());
        assertEquals(50.0, itemSessaoTreino.getCargaRealizada());
    }

    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID exercício
        itemSessaoTreino.setIdExercicio(2);
        assertEquals(2, itemSessaoTreino.getIdExercicio());
        //Repetições realizadas
        itemSessaoTreino.setRepeticoesRealizadas(15);
        assertEquals(15, itemSessaoTreino.getRepeticoesRealizadas());
        //Carga realizada
        itemSessaoTreino.setCargaRealizada(60.0);
        assertEquals(60.0, itemSessaoTreino.getCargaRealizada());
    }

    //testar toString
    @Test
    @DisplayName("Teste do método toString")
    void testToString() {
        String expected = "ID Exercício: 1, Repetições: 10, Carga: 50.0kg";
        assertEquals(expected, itemSessaoTreino.toString());
    }

    //testar valores nulos
    @Test
    @DisplayName("Teste de valores nulos")
    void testValoresNulos() {
        assertNotNull(itemSessaonulo);
        assertEquals(0, itemSessaonulo.getIdExercicio());
        assertEquals(0, itemSessaonulo.getRepeticoesRealizadas());
        assertEquals(0.0, itemSessaonulo.getCargaRealizada());
    }
    //testar valores limites
    @Test
    @DisplayName("Teste de valores limites")
    void testValoresLimites() {
        itemSessaoTreino.setRepeticoesRealizadas(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, itemSessaoTreino.getRepeticoesRealizadas());
        itemSessaoTreino.setCargaRealizada(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, itemSessaoTreino.getCargaRealizada());
    }

}