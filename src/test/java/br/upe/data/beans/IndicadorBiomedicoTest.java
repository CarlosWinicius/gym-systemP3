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
        indicadorCompleto = new IndicadorBiomedico(1, 100, java.time.LocalDate.of(2023, 10, 1), 70.5, 175.0, 15.0, 85.0, 23.0);
        indicadorSemId = new IndicadorBiomedico(100, java.time.LocalDate.of(2023, 10, 1), 70.5, 175.0, 15.0, 85.0, 23.0);
        indicadorNull = new IndicadorBiomedico(1, 100, null, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    //testar construtores
    @Test
    @DisplayName("Teste do construtor completo")
    void testConstrutorCompleto() {
        assert indicadorCompleto.getId() == 1;
        assert indicadorCompleto.getIdUsuario() == 100;
        assert indicadorCompleto.getData().equals(java.time.LocalDate.of(2023, 10, 1));
        assert indicadorCompleto.getPesoKg() == 70.5;
        assert indicadorCompleto.getAlturaCm() == 175.0;
        assert indicadorCompleto.getPercentualGordura() == 15.0;
        assert indicadorCompleto.getPercentualMassaMagra() == 85.0;
        assert indicadorCompleto.getImc() == 23.0;
    }
    @Test
    @DisplayName("Teste do construtor sem ID")
    void testConstrutorSemId() {
        assert indicadorSemId.getId() == 0; // ID padrão é 0
        assert indicadorSemId.getIdUsuario() == 100;
        assert indicadorSemId.getData().equals(java.time.LocalDate.of(2023, 10, 1));
        assert indicadorSemId.getPesoKg() == 70.5;
        assert indicadorSemId.getAlturaCm() == 175.0;
        assert indicadorSemId.getPercentualGordura() == 15.0;
        assert indicadorSemId.getPercentualMassaMagra() == 85.0;
        assert indicadorSemId.getImc() == 23.0;
    }
    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters")
    void testGettersAndSetters() {
        //ID
        indicadorCompleto.setId(2);
        assert indicadorCompleto.getId() == 2;
        //ID usuário
        indicadorCompleto.setIdUsuario(200);
        assert indicadorCompleto.getIdUsuario() == 200;
        //data
        indicadorCompleto.setData(java.time.LocalDate.of(2023, 11, 1));
        assert indicadorCompleto.getData().equals(java.time.LocalDate.of(2023, 11, 1));
        //peso em kg
        indicadorCompleto.setPesoKg(75.0);
        assert indicadorCompleto.getPesoKg() == 75.0;
        //altura em cm
        indicadorCompleto.setAlturaCm(180.0);
        assert indicadorCompleto.getAlturaCm() == 180.0;
        //percentual de gordura
        indicadorCompleto.setPercentualGordura(20.0);
        assert indicadorCompleto.getPercentualGordura() == 20.0;
        //percentual de massa magra
        indicadorCompleto.setPercentualMassaMagra(80.0);
        assert indicadorCompleto.getPercentualMassaMagra() == 80.0;
        //IMC
        indicadorCompleto.setImc(23.5);
        assert indicadorCompleto.getImc() == 23.5;
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
        assert indicadorNull.getPesoKg() == -10.0;
        assert indicadorNull.getAlturaCm() == -150.0;
        assert indicadorNull.getPercentualGordura() == -5.0;
        assert indicadorNull.getPercentualMassaMagra() == -95.0;
        assert indicadorNull.getImc() == -20.0;
    }

    //testar o toString
    @Test
    @DisplayName("Teste do método toString")
    void testToString() {
        String expected = "ID: 1 | Data: 2023-10-01 | Peso: 70,5kg | Altura: 175cm | Gordura: 15,0% | Massa Magra: 85,0% | IMC: 23,00";
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
        assert indicadorCompleto.getPesoKg() == 0.0;
        indicadorCompleto.setPesoKg(500.0); // valor extremamente alto
        assert indicadorCompleto.getPesoKg() == 500.0;
        //altura em cm
        indicadorCompleto.setAlturaCm(0.0);
        assert indicadorCompleto.getAlturaCm() == 0.0;
        indicadorCompleto.setAlturaCm(300.0); // valor extremamente alto
        assert indicadorCompleto.getAlturaCm() == 300.0;
        //percentual de gordura
        indicadorCompleto.setPercentualGordura(0.0);
        assert indicadorCompleto.getPercentualGordura() == 0.0;
        indicadorCompleto.setPercentualGordura(100.0); // valor máximo possível
        assert indicadorCompleto.getPercentualGordura() == 100.0;
        //percentual de massa magra
        indicadorCompleto.setPercentualMassaMagra(0.0);
        assert indicadorCompleto.getPercentualMassaMagra() == 0.0;
        indicadorCompleto.setPercentualMassaMagra(100.0); // valor máximo possível
        assert indicadorCompleto.getPercentualMassaMagra() == 100.0;
        //IMC
        indicadorCompleto.setImc(0.0);
        assert indicadorCompleto.getImc() == 0.0;
        indicadorCompleto.setImc(100.0); // valor extremamente alto
        assert indicadorCompleto.getImc() == 100.0;
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