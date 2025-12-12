package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IndicadorBiomedicoTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);
    }

    @Test
    @DisplayName("Deve criar indicador com construtor vazio")
    void testCriarIndicadorComConstrutorVazio() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();

        assertNotNull(indicador);
        assertNull(indicador.getId());
        assertNull(indicador.getUsuario());
        assertNull(indicador.getDataRegistro());
        assertNull(indicador.getPesoKg());
        assertNull(indicador.getAlturaCm());
        assertNull(indicador.getPercentualGordura());
        assertNull(indicador.getPercentualMassaMagra());
        assertNull(indicador.getImc());
    }

    @Test
    @DisplayName("Deve criar indicador com construtor completo")
    void testCriarIndicadorComConstrutorCompleto() {
        LocalDate data = LocalDate.of(2023, 6, 15);
        IndicadorBiomedico indicador = new IndicadorBiomedico(
                usuario, data, 70.0, 175.0, 15.0, 60.0, 22.86
        );

        assertNotNull(indicador);
        assertEquals(usuario, indicador.getUsuario());
        assertEquals(data, indicador.getDataRegistro());
        assertEquals(70.0, indicador.getPesoKg());
        assertEquals(175.0, indicador.getAlturaCm());
        assertEquals(15.0, indicador.getPercentualGordura());
        assertEquals(60.0, indicador.getPercentualMassaMagra());
        assertEquals(22.86, indicador.getImc());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        LocalDate data = LocalDate.now();

        indicador.setId(10);
        indicador.setUsuario(usuario);
        indicador.setDataRegistro(data);
        indicador.setPesoKg(75.0);
        indicador.setAlturaCm(180.0);
        indicador.setPercentualGordura(12.0);
        indicador.setPercentualMassaMagra(65.0);
        indicador.setImc(23.15);

        assertEquals(10, indicador.getId());
        assertEquals(usuario, indicador.getUsuario());
        assertEquals(data, indicador.getDataRegistro());
        assertEquals(75.0, indicador.getPesoKg());
        assertEquals(180.0, indicador.getAlturaCm());
        assertEquals(12.0, indicador.getPercentualGordura());
        assertEquals(65.0, indicador.getPercentualMassaMagra());
        assertEquals(23.15, indicador.getImc());
    }

    @Test
    @DisplayName("Deve manter relacionamento com usuário")
    void testRelacionamentoComUsuario() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        indicador.setUsuario(usuario);

        assertNotNull(indicador.getUsuario());
        assertEquals(1, indicador.getUsuario().getId());
        assertEquals("Test User", indicador.getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve permitir valores decimais para peso e altura")
    void testValoresDecimais() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        indicador.setPesoKg(72.5);
        indicador.setAlturaCm(177.3);

        assertEquals(72.5, indicador.getPesoKg());
        assertEquals(177.3, indicador.getAlturaCm());
    }

    @Test
    @DisplayName("Deve permitir valores decimais para percentuais")
    void testPercentuaisDecimais() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        indicador.setPercentualGordura(14.7);
        indicador.setPercentualMassaMagra(62.3);

        assertEquals(14.7, indicador.getPercentualGordura());
        assertEquals(62.3, indicador.getPercentualMassaMagra());
    }

    @Test
    @DisplayName("Deve permitir registrar data específica")
    void testDataEspecifica() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        LocalDate dataEspecifica = LocalDate.of(2023, 1, 15);
        indicador.setDataRegistro(dataEspecifica);

        assertEquals(dataEspecifica, indicador.getDataRegistro());
        assertEquals(2023, indicador.getDataRegistro().getYear());
        assertEquals(1, indicador.getDataRegistro().getMonthValue());
        assertEquals(15, indicador.getDataRegistro().getDayOfMonth());
    }

    @Test
    @DisplayName("Deve gerar toString sem erro")
    void testToString() {
        IndicadorBiomedico indicador = new IndicadorBiomedico();
        indicador.setId(1);
        indicador.setUsuario(usuario);
        indicador.setDataRegistro(LocalDate.now());
        indicador.setPesoKg(70.0);
        indicador.setAlturaCm(175.0);
        indicador.setImc(22.86);

        String resultado = indicador.toString();

        assertNotNull(resultado);
        // Lombok gera toString com exclude do usuario para evitar loop
    }
}

