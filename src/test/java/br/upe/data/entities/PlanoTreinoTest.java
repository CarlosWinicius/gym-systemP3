package br.upe.data.entities;

import br.upe.data.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanoTreinoTest {

    private Usuario usuario;
    private Exercicio exercicio;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Test User", "test@email.com", "senha123", TipoUsuario.COMUM);
        usuario.setId(1);

        exercicio = new Exercicio();
        exercicio.setId(1);
        exercicio.setUsuario(usuario);
        exercicio.setNome("Supino");
        exercicio.setDescricao("Exercício para peitoral");
        exercicio.setCaminhoGif("/gifs/supino.gif");
    }

    @Test
    @DisplayName("Deve criar plano de treino com construtor vazio")
    void testCriarPlanoComConstrutorVazio() {
        PlanoTreino plano = new PlanoTreino();

        assertNotNull(plano);
        assertNull(plano.getId());
        assertNull(plano.getUsuario());
        assertNull(plano.getNome());
        assertNotNull(plano.getItensTreino());
        assertTrue(plano.getItensTreino().isEmpty());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos")
    void testSetEGetCampos() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(10);
        plano.setUsuario(usuario);
        plano.setNome("Plano A");

        assertEquals(10, plano.getId());
        assertEquals(usuario, plano.getUsuario());
        assertEquals("Plano A", plano.getNome());
    }

    @Test
    @DisplayName("Deve adicionar item ao plano de treino")
    void testAdicionarItem() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano A");

        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);
        item.setCargaKg(50);
        item.setRepeticoes(10);

        plano.adicionarItem(item);

        assertEquals(1, plano.getItensTreino().size());
        assertEquals(item, plano.getItensTreino().get(0));
        assertEquals(plano, item.getPlanoTreino());
    }

    @Test
    @DisplayName("Deve adicionar múltiplos itens ao plano")
    void testAdicionarMultiplosItens() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano B");

        ItemPlanoTreino item1 = new ItemPlanoTreino();
        item1.setExercicio(exercicio);
        item1.setCargaKg(50);
        item1.setRepeticoes(10);

        ItemPlanoTreino item2 = new ItemPlanoTreino();
        item2.setExercicio(exercicio);
        item2.setCargaKg(60);
        item2.setRepeticoes(8);

        plano.adicionarItem(item1);
        plano.adicionarItem(item2);

        assertEquals(2, plano.getItensTreino().size());
    }

    @Test
    @DisplayName("Deve manter relacionamento com usuário")
    void testRelacionamentoComUsuario() {
        PlanoTreino plano = new PlanoTreino();
        plano.setUsuario(usuario);

        assertNotNull(plano.getUsuario());
        assertEquals(1, plano.getUsuario().getId());
        assertEquals("Test User", plano.getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve vincular item ao plano ao adicionar")
    void testVincularItemAoPlanoAoAdicionar() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);

        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);

        assertNull(item.getPlanoTreino());

        plano.adicionarItem(item);

        assertNotNull(item.getPlanoTreino());
        assertEquals(plano, item.getPlanoTreino());
    }

    @Test
    @DisplayName("Deve gerar toString com informações do plano")
    void testToString() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano Test");

        String resultado = plano.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("ID Plano"));
        assertTrue(resultado.contains("Plano Test"));
    }

    @Test
    @DisplayName("Deve exibir mensagem quando plano não tem exercícios")
    void testToStringPlanoVazio() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano Vazio");

        String resultado = plano.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("não possui exercícios"));
    }

    @Test
    @DisplayName("Deve exibir exercícios quando plano tem itens")
    void testToStringComExercicios() {
        PlanoTreino plano = new PlanoTreino();
        plano.setId(1);
        plano.setUsuario(usuario);
        plano.setNome("Plano Com Exercícios");

        ItemPlanoTreino item = new ItemPlanoTreino();
        item.setExercicio(exercicio);
        item.setCargaKg(50);
        item.setRepeticoes(10);

        plano.adicionarItem(item);

        String resultado = plano.toString();

        assertNotNull(resultado);
        assertTrue(resultado.contains("Exercícios no Plano"));
    }

    @Test
    @DisplayName("Deve inicializar lista vazia de itens ao criar")
    void testInicializarListaVazia() {
        PlanoTreino plano = new PlanoTreino();

        assertNotNull(plano.getItensTreino());
        assertEquals(0, plano.getItensTreino().size());
    }
}

