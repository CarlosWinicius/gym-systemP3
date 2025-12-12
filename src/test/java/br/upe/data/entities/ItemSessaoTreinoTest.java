package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ItemSessaoTreinoTest {

    private SessaoTreino sessao;
    private Exercicio exercicio;
    private Usuario usuario;
    private PlanoTreino plano;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);

        plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano A");

        sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

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
        ItemSessaoTreino item = new ItemSessaoTreino();

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getSessaoTreino());
        assertNull(item.getExercicio());
        assertNull(item.getRepeticoesRealizadas());
        assertNull(item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        ItemSessaoTreino item = new ItemSessaoTreino();

        item.setId(10);
        item.setSessaoTreino(sessao);
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        assertEquals(10, item.getId());
        assertEquals(sessao, item.getSessaoTreino());
        assertEquals(exercicio, item.getExercicio());
        assertEquals(12, item.getRepeticoesRealizadas());
        assertEquals(55.0, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve manter relacionamento com sessão de treino")
    void testRelacionamentoComSessao() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setSessaoTreino(sessao);

        assertNotNull(item.getSessaoTreino());
        assertEquals(1, item.getSessaoTreino().getId());
    }

    @Test
    @DisplayName("Deve manter relacionamento com exercício")
    void testRelacionamentoComExercicio() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);

        assertNotNull(item.getExercicio());
        assertEquals(1, item.getExercicio().getId());
        assertEquals("Supino", item.getExercicio().getNome());
    }

    @Test
    @DisplayName("Deve permitir valores decimais para carga realizada")
    void testCargaDecimal() {
        ItemSessaoTreino item = new ItemSessaoTreino();

        item.setCargaRealizada(52.5);
        assertEquals(52.5, item.getCargaRealizada());

        item.setCargaRealizada(47.75);
        assertEquals(47.75, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve permitir diferentes valores de repetições realizadas")
    void testDiferentesValoresRepeticoes() {
        ItemSessaoTreino item = new ItemSessaoTreino();

        item.setRepeticoesRealizadas(8);
        assertEquals(8, item.getRepeticoesRealizadas());

        item.setRepeticoesRealizadas(15);
        assertEquals(15, item.getRepeticoesRealizadas());
    }

    @Test
    @DisplayName("Deve gerar toString com informações do item")
    void testToString() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        String resultado = item.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Exercício"));
        assertTrue(resultado.contains("Supino"));
        assertTrue(resultado.contains("12"));
        assertTrue(resultado.contains("55"));
    }

    @Test
    @DisplayName("Deve gerar toString mesmo sem exercício definido")
    void testToStringSemExercicio() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        String resultado = item.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Desconhecido"));
    }

    @Test
    @DisplayName("Deve permitir registrar execução com valores altos")
    void testValoresAltos() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setRepeticoesRealizadas(50);
        item.setCargaRealizada(200.0);

        assertEquals(50, item.getRepeticoesRealizadas());
        assertEquals(200.0, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve permitir registrar execução com valores baixos")
    void testValoresBaixos() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setRepeticoesRealizadas(1);
        item.setCargaRealizada(5.0);

        assertEquals(1, item.getRepeticoesRealizadas());
        assertEquals(5.0, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve permitir registrar execução sem carga (peso corporal)")
    void testExecucaoSemCarga() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(20);
        item.setCargaRealizada(0.0);

        assertEquals(20, item.getRepeticoesRealizadas());
        assertEquals(0.0, item.getCargaRealizada());
    }

    @Test
    @DisplayName("Deve permitir alterar valores após criação")
    void testAlterarValores() {
        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setRepeticoesRealizadas(10);
        item.setCargaRealizada(50.0);

        assertEquals(10, item.getRepeticoesRealizadas());
        assertEquals(50.0, item.getCargaRealizada());

        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        assertEquals(12, item.getRepeticoesRealizadas());
        assertEquals(55.0, item.getCargaRealizada());
    }
}

