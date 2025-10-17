package br.upe.data.beans;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemPlanoTreinoTest {
    @InjectMocks
    private ItemPlanoTreino itemPlanoTreino;
    private ItemPlanoTreino itemNulo;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        itemPlanoTreino = new ItemPlanoTreino(1, 50, 10);
        itemNulo = new ItemPlanoTreino(0, 0, 0);
    }

    // Testar construtor
    @Test
    @DisplayName("Teste do construtor")
    void testConstrutor() {
        assertEquals(1, itemPlanoTreino.getIdExercicio());
        assertEquals(50, itemPlanoTreino.getCargaKg());
        assertEquals(10, itemPlanoTreino.getRepeticoes());
    }

    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID exercício
        itemPlanoTreino.setIdExercicio(2);
        assertEquals(2, itemPlanoTreino.getIdExercicio());
        //Carga em kg
        itemPlanoTreino.setCargaKg(60);
        assertEquals(60, itemPlanoTreino.getCargaKg());
        //Repetições
        itemPlanoTreino.setRepeticoes(15);
        assertEquals(15, itemPlanoTreino.getRepeticoes());
    }

    //testar toString
    @Test
    @DisplayName("Teste do toString")
    void testToString() {
        String expected = "ID Exercício: 1, Carga: 50kg, Repetições: 10";
        assertEquals(expected, itemPlanoTreino.toString());
    }

    //testar valores
    @Test
    @DisplayName("Teste de valores nulos")
    void testValoresNulos() {
        // Valores padrão são 0
        assertEquals(0, itemNulo.getIdExercicio());
        assertEquals(0, itemNulo.getCargaKg());
        assertEquals(0, itemNulo.getRepeticoes());
    }

    @Test
    @DisplayName("Teste de valores limite")
    void testValoresLimite() {
        itemPlanoTreino.setCargaKg(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, itemPlanoTreino.getCargaKg());
        itemPlanoTreino.setRepeticoes(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, itemPlanoTreino.getRepeticoes());
    }

    @Test
    @DisplayName("Teste de valores negativos")
    void testValoresNegativos() {
        itemPlanoTreino.setCargaKg(-10);
        assertEquals(-10, itemPlanoTreino.getCargaKg());
        itemPlanoTreino.setRepeticoes(-5);
        assertEquals(-5, itemPlanoTreino.getRepeticoes());
    }

}