package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlanoTreinoBeanTest {

    @Mock
    private ItemPlanoTreino itemMock;
    @Mock
    private ItemPlanoTreino itemMock2;
    @Mock
    private List<ItemPlanoTreino> listaMock;
    private List<ItemPlanoTreino> listaVazia;

    @InjectMocks
    private PlanoTreinoBean planoTreinoBean;
    private PlanoTreinoBean planoVazio;

    @BeforeEach
    void setUp() {
        listaMock = new ArrayList<>(Arrays.asList(itemMock, itemMock2));
        listaVazia = new ArrayList<>();
        planoTreinoBean = new PlanoTreinoBean(1,100,"Jão",listaMock);
        planoVazio = new PlanoTreinoBean(2, 101, "Ana", listaVazia);
    }

    //testar construtor
    @Test
    @DisplayName("Teste do construtor de PlanoTreino")
    void testConstrutor() {
        assertNotNull(planoTreinoBean);
        assertSame(listaMock, planoTreinoBean.getItensTreino());
    }
    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters de PlanoTreino")
    void testGettersAndSetters() {
        planoTreinoBean.setIdPlano(2);
        assertEquals(2, planoTreinoBean.getIdPlano());
        planoTreinoBean.setIdUsuario(200);
        assertEquals(200, planoTreinoBean.getIdUsuario());
        planoTreinoBean.setNome("Maria");
        assertEquals("Maria", planoTreinoBean.getNome());
        List<ItemPlanoTreino> novaLista = new ArrayList<>();
        planoTreinoBean.setItensTreino(novaLista);
        assertSame(novaLista, planoTreinoBean.getItensTreino());
    }

    //testar adicionar item ao plano
    @Test
    @DisplayName("Teste do método adicionarItem de PlanoTreino")
    void testAdicionarItem() {
        ItemPlanoTreino novoItem = new ItemPlanoTreino(1, 3, 10);
        planoTreinoBean.adicionarItem(novoItem);
        assertTrue(planoTreinoBean.getItensTreino().contains(novoItem));
    }

    //testar toString
    @Test
    @DisplayName("Teste do método toString de PlanoTreino")
    void testToString() {
        StringBuilder expected = new StringBuilder();
        expected.append("ID Plano: ").append(1)
                .append(", ID Usuário: ").append(100)
                .append(", Nome: ").append("'Jão'").append("\n")
                .append("  Exercícios no Plano:\n")
                .append("    ").append(1).append(". ").append(itemMock.toString()).append("\n")
                .append("    ").append(2).append(". ").append(itemMock2.toString()).append("\n");
        assertEquals(expected.toString(), planoTreinoBean.toString());
    }

    //testar toString com lista vazia
    @Test
    @DisplayName("Teste do método toString de PlanoTreino com lista vazia")
    void testToStringEmptyList() {
        StringBuilder expected = new StringBuilder();
        expected.append("ID Plano: ").append(2)
                .append(", ID Usuário: ").append(101)
                .append(", Nome: ").append("'Ana'").append("\n")
                .append("  [Este plano não possui exercícios ainda.]");
        assertEquals(expected.toString(), planoVazio.toString());
    }

}