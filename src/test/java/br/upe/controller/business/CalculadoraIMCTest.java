package br.upe.controller.business;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraIMCTest {

    @Test
    @DisplayName("Deve calcular IMC corretamente")
    void testCalcular_Success() {
        double imc = CalculadoraIMC.calcular(70.0, 175.0);
        assertEquals(22.86, imc, 0.01);
    }

    @Test
    @DisplayName("Deve lançar exceção para altura zero")
    void testCalcular_AlturaZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CalculadoraIMC.calcular(70.0, 0);
        });

        assertEquals("Altura deve ser maior que zero para calcular o IMC.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve classificar IMC como abaixo do peso")
    void testClassificarImc_AbaixoPeso() {
        String classificacao = CalculadoraIMC.classificarImc(18.0);
        assertEquals("Abaixo do peso", classificacao);
    }

    @Test
    @DisplayName("Deve classificar IMC como peso normal")
    void testClassificarImc_PesoNormal() {
        String classificacao = CalculadoraIMC.classificarImc(22.0);
        assertEquals("Peso normal", classificacao);
    }

    @Test
    @DisplayName("Deve classificar IMC como sobrepeso")
    void testClassificarImc_Sobrepeso() {
        String classificacao = CalculadoraIMC.classificarImc(27.0);
        assertEquals("Sobrepeso", classificacao);
    }

    @Test
    @DisplayName("Deve classificar IMC como obesidade grau I")
    void testClassificarImc_ObesidadeI() {
        String classificacao = CalculadoraIMC.classificarImc(32.0);
        assertEquals("Obesidade Grau I", classificacao);
    }

    @Test
    @DisplayName("Deve classificar IMC como obesidade grau II")
    void testClassificarImc_ObesidadeII() {
        String classificacao = CalculadoraIMC.classificarImc(37.0);
        assertEquals("Obesidade Grau II", classificacao);
    }

    @Test
    @DisplayName("Deve classificar IMC como obesidade grau III")
    void testClassificarImc_ObesidadeIII() {
        String classificacao = CalculadoraIMC.classificarImc(42.0);
        assertEquals("Obesidade Grau III", classificacao);
    }
}