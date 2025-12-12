package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IndicadorBiomedicoTest {

    @InjectMocks
    private IndicadorBiomedico indicadorCompleto;
    private IndicadorBiomedico indicadorSemId;
    private IndicadorBiomedico indicadorNull;

    @BeforeEach
    void setUp() {
        //criando objetos de IndicadorBiomedico para os testes
        indicadorCompleto = new IndicadorBiomedico(100, java.time.LocalDate.of(2023, 10, 1), 70.5, 175.0, 15.0, 85.0, 23.0);
        indicadorSemId = new IndicadorBiomedico(100, java.time.LocalDate.of(2023, 10, 1), 70.5, 175.0, 15.0, 85.0, 23.0);
        indicadorNull = new IndicadorBiomedico(0, null, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    //testar construtores
    @Test
    @DisplayName("Teste do construtor completo")
    void testConstrutorCompleto() {
        assertEquals(100, indicadorCompleto.getIdUsuario());
        assertEquals(java.time.LocalDate.of(2023, 10, 1), indicadorCompleto.getData());
        assertEquals(70.5, indicadorCompleto.getPesoKg());
        assertEquals(175.0, indicadorCompleto.getAlturaCm());
        assertEquals(15.0, indicadorCompleto.getPercentualGordura());
        assertEquals(85.0, indicadorCompleto.getPercentualMassaMagra());
        assertEquals(23.0, indicadorCompleto.getImc());
    }
    @Test
    @DisplayName("Teste do construtor sem ID")
    void testConstrutorSemId() {
        assertEquals(100, indicadorSemId.getIdUsuario());
        assertEquals(java.time.LocalDate.of(2023, 10, 1), indicadorSemId.getData());
        assertEquals(70.5, indicadorSemId.getPesoKg());
        assertEquals(175.0, indicadorSemId.getAlturaCm());
        assertEquals(15.0, indicadorSemId.getPercentualGordura());
        assertEquals(85.0, indicadorSemId.getPercentualMassaMagra());
        assertEquals(23.0, indicadorSemId.getImc());
    }
    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID usuário
        indicadorCompleto.setIdUsuario(200);
        assertEquals(200, indicadorCompleto.getIdUsuario());
        //data
        indicadorCompleto.setData(java.time.LocalDate.of(2023, 11, 1));
        assertEquals(java.time.LocalDate.of(2023, 11, 1), indicadorCompleto.getData());
        //peso em kg
        indicadorCompleto.setPesoKg(75.0);
        assertEquals(75.0, indicadorCompleto.getPesoKg());
        //altura em cm
        indicadorCompleto.setAlturaCm(180.0);
        assertEquals(180.0, indicadorCompleto.getAlturaCm());
        //percentual de gordura
        indicadorCompleto.setPercentualGordura(20.0);
        assertEquals(20.0, indicadorCompleto.getPercentualGordura());
        //percentual de massa magra
        indicadorCompleto.setPercentualMassaMagra(80.0);
        assertEquals(80.0, indicadorCompleto.getPercentualMassaMagra());
        //IMC
        indicadorCompleto.setImc(23.5);
        assertEquals(23.5, indicadorCompleto.getImc());
    }

    //testar valores nulos
    @Test
    @DisplayName("Teste de valores nulos")
    void testValoresNulosOuInvalidos() {
        //data nula
        assertNull(indicadorNull.getData());
        //atribuindo valores nulos via setters
        indicadorNull.setData(null);
        //verifica se o valor nulo foi mantido
        assertNull(indicadorNull.getData());
        //atribuindo valores inválidos via setters
        indicadorNull.setPesoKg(-10.0);
        indicadorNull.setAlturaCm(-150.0);
        indicadorNull.setPercentualGordura(-5.0);
        indicadorNull.setPercentualMassaMagra(-95.0);
        indicadorNull.setImc(-20.0);
        //verifica se os valores inválidos foram atribuídos (não há validação no setter)
        assertEquals(-10.0, indicadorNull.getPesoKg());
        assertEquals(-150.0, indicadorNull.getAlturaCm());
        assertEquals(-5.0, indicadorNull.getPercentualGordura());
        assertEquals(-95.0, indicadorNull.getPercentualMassaMagra());
        assertEquals(-20.0, indicadorNull.getImc());
    }

    //testar o toString
    @Test
    @DisplayName("Teste do método toString")
    void testToString() {
        String expected = "ID: 0 | Data: 2023-10-01 | Peso: 70,5kg | Altura: 175cm | Gordura: 15,0% | Massa Magra: 85,0% | IMC: 23,00";
        assertEquals(expected, indicadorCompleto.toString());
    }
    @Test
    @DisplayName("Teste do método toString sem ID")
    void testToStringSemId() {
        // ID padrão é 0
        String expected = "ID: 0 | Data: 2023-10-01 | Peso: 70,5kg | Altura: 175cm | Gordura: 15,0% | Massa Magra: 85,0% | IMC: 23,00";
        assertEquals(expected, indicadorSemId.toString());
    }


    //testes de valores limites
    @Test
    @DisplayName("Teste de valores limites")
    void testValoresLimites() {
        //peso em kg
        indicadorCompleto.setPesoKg(0.0);
        assertEquals(0.0, indicadorCompleto.getPesoKg());
        indicadorCompleto.setPesoKg(500.0); // valor extremamente alto
        assertEquals(500.0, indicadorCompleto.getPesoKg());
        //altura em cm
        indicadorCompleto.setAlturaCm(0.0);
        assertEquals(0.0, indicadorCompleto.getAlturaCm());
        indicadorCompleto.setAlturaCm(300.0); // valor extremamente alto
        assertEquals(300.0, indicadorCompleto.getAlturaCm());
        //percentual de gordura
        indicadorCompleto.setPercentualGordura(0.0);
        assertEquals(0.0, indicadorCompleto.getPercentualGordura());
        indicadorCompleto.setPercentualGordura(100.0); // valor máximo possível
        assertEquals(100.0, indicadorCompleto.getPercentualGordura());
        //percentual de massa magra
        indicadorCompleto.setPercentualMassaMagra(0.0);
        assertEquals(0.0, indicadorCompleto.getPercentualMassaMagra());
        indicadorCompleto.setPercentualMassaMagra(100.0); // valor máximo possível
        assertEquals(100.0, indicadorCompleto.getPercentualMassaMagra());
        //IMC
        indicadorCompleto.setImc(0.0);
        assertEquals(0.0, indicadorCompleto.getImc());
        indicadorCompleto.setImc(100.0); // valor extremamente alto
        assertEquals(100.0, indicadorCompleto.getImc());
    }

    //testes de consistências
    @Test
    @DisplayName("Teste de consistência entre percentual de gordura e massa magra")
    void testConsistenciaPercentuais() {
        indicadorCompleto.setPercentualGordura(30.0);
        indicadorCompleto.setPercentualMassaMagra(70.0);
        assertEquals(100.0, indicadorCompleto.getPercentualGordura() + indicadorCompleto.getPercentualMassaMagra(), 0.01);
    }
    @Test
    @DisplayName("Teste de consistência entre peso, altura e IMC")
    void testConsistenciaImc() {
        indicadorCompleto.setPesoKg(80.0);
        indicadorCompleto.setAlturaCm(180.0);
        double alturaM = indicadorCompleto.getAlturaCm() / 100.0;
        double imcCalculado = indicadorCompleto.getPesoKg() / (alturaM * alturaM);
        indicadorCompleto.setImc(imcCalculado);
        assertEquals(imcCalculado, indicadorCompleto.getImc(), 0.01);
    }

}