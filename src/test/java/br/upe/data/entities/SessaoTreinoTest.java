package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SessaoTreinoTest {

    private Usuario usuario;
    private PlanoTreino plano;
    private Exercicio exercicio;

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
    @DisplayName("Deve criar sessão de treino com construtor vazio")
    void testCriarSessaoComConstrutorVazio() {
        SessaoTreino sessao = new SessaoTreino();

        assertNotNull(sessao);
        assertNull(sessao.getId());
        assertNull(sessao.getUsuario());
        assertNull(sessao.getPlanoTreino());
        assertNull(sessao.getDataSessao());
        assertNotNull(sessao.getItensExecutados());
        assertTrue(sessao.getItensExecutados().isEmpty());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        SessaoTreino sessao = new SessaoTreino();
        LocalDate data = LocalDate.now();

        sessao.setId(10);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(data);

        assertEquals(10, sessao.getId());
        assertEquals(usuario, sessao.getUsuario());
        assertEquals(plano, sessao.getPlanoTreino());
        assertEquals(data, sessao.getDataSessao());
    }

    @Test
    @DisplayName("Deve adicionar item executado à sessão")
    void testAdicionarItemExecutado() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        sessao.adicionarItemExecutado(item);

        assertEquals(1, sessao.getItensExecutados().size());
        assertEquals(item, sessao.getItensExecutados().get(0));
        assertEquals(sessao, item.getSessaoTreino());
    }

    @Test
    @DisplayName("Deve adicionar múltiplos itens executados")
    void testAdicionarMultiplosItensExecutados() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);

        ItemSessaoTreino item1 = new ItemSessaoTreino();
        item1.setExercicio(exercicio);
        item1.setRepeticoesRealizadas(12);
        item1.setCargaRealizada(55.0);

        ItemSessaoTreino item2 = new ItemSessaoTreino();
        item2.setExercicio(exercicio);
        item2.setRepeticoesRealizadas(10);
        item2.setCargaRealizada(60.0);

        sessao.adicionarItemExecutado(item1);
        sessao.adicionarItemExecutado(item2);

        assertEquals(2, sessao.getItensExecutados().size());
    }

    @Test
    @DisplayName("Deve manter relacionamento com usuário")
    void testRelacionamentoComUsuario() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setUsuario(usuario);

        assertNotNull(sessao.getUsuario());
        assertEquals(1, sessao.getUsuario().getId());
        assertEquals("Test User", sessao.getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve manter relacionamento com plano de treino")
    void testRelacionamentoComPlanoTreino() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setPlanoTreino(plano);

        assertNotNull(sessao.getPlanoTreino());
        assertEquals(1, sessao.getPlanoTreino().getId());
        assertEquals("Plano A", sessao.getPlanoTreino().getNome());
    }

    @Test
    @DisplayName("Deve vincular item à sessão ao adicionar")
    void testVincularItemASessaoAoAdicionar() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);

        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);

        assertNull(item.getSessaoTreino());

        sessao.adicionarItemExecutado(item);

        assertNotNull(item.getSessaoTreino());
        assertEquals(sessao, item.getSessaoTreino());
    }

    @Test
    @DisplayName("Deve gerar toString com informações da sessão")
    void testToString() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

        String resultado = sessao.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("ID Sessão"));
        assertTrue(resultado.contains("Plano A"));
    }

    @Test
    @DisplayName("Deve exibir mensagem quando sessão não tem exercícios registrados")
    void testToStringSessaoVazia() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

        String resultado = sessao.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Nenhum exercício registrado"));
    }

    @Test
    @DisplayName("Deve exibir exercícios quando sessão tem itens executados")
    void testToStringComExercicios() {
        SessaoTreino sessao = new SessaoTreino();
        sessao.setId(1);
        sessao.setUsuario(usuario);
        sessao.setPlanoTreino(plano);
        sessao.setDataSessao(LocalDate.now());

        ItemSessaoTreino item = new ItemSessaoTreino();
        item.setExercicio(exercicio);
        item.setRepeticoesRealizadas(12);
        item.setCargaRealizada(55.0);

        sessao.adicionarItemExecutado(item);

        String resultado = sessao.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Exercícios Registrados"));
    }

    @Test
    @DisplayName("Deve inicializar lista vazia de itens executados ao criar")
    void testInicializarListaVazia() {
        SessaoTreino sessao = new SessaoTreino();

        assertNotNull(sessao.getItensExecutados());
        assertEquals(0, sessao.getItensExecutados().size());
    }

    @Test
    @DisplayName("Deve permitir registrar data específica")
    void testDataEspecifica() {
        SessaoTreino sessao = new SessaoTreino();
        LocalDate dataEspecifica = LocalDate.of(2023, 6, 15);
        sessao.setDataSessao(dataEspecifica);

        assertEquals(dataEspecifica, sessao.getDataSessao());
        assertEquals(2023, sessao.getDataSessao().getYear());
        assertEquals(6, sessao.getDataSessao().getMonthValue());
        assertEquals(15, sessao.getDataSessao().getDayOfMonth());
    }
}

