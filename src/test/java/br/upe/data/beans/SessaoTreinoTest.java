package br.upe.data.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SessaoTreinoTest {
    @Mock
    private ItemSessaoTreino itemMock;
    @Mock
    private ItemSessaoTreino itemMock2;
    @Mock
    private java.util.List<ItemSessaoTreino> listaMock;
    private java.util.List<ItemSessaoTreino> listaVazia;

    @InjectMocks
    private SessaoTreino sessaoTreino;
    private SessaoTreino sessaoVazia;

    @BeforeEach
    void setUp() {
        listaMock = new ArrayList<>(Arrays.asList(itemMock, itemMock2));
        listaVazia = new ArrayList<>();
        sessaoTreino = new SessaoTreino(1, 100, 200, LocalDate.now(), listaMock);
        sessaoVazia = new SessaoTreino(2, 101, 201, LocalDate.now(), listaVazia);
    }

    // Testar construtor
    @Test
    @DisplayName("Teste do construtor de SessaoTreino")
    void testConstrutor() {
        assertNotNull(sessaoTreino);
        assertSame(listaMock, sessaoTreino.getItensExecutados());
    }

    //testar getters e setters
    @Test
    @DisplayName("Teste dos getters e setters de SessaoTreino")
    void testGettersAndSetters() {
        sessaoTreino.setIdSessao(2);
        assertEquals(2, sessaoTreino.getIdSessao());
        sessaoTreino.setIdUsuario(200);
        assertEquals(200, sessaoTreino.getIdUsuario());
        sessaoTreino.setIdPlanoTreino(300);
        assertEquals(300, sessaoTreino.getIdPlanoTreino());
        LocalDate novaData = LocalDate.of(2023, 1, 1);
        sessaoTreino.setDataSessao(novaData);
        assertEquals(novaData, sessaoTreino.getDataSessao());
        java.util.List<ItemSessaoTreino> novaLista = new ArrayList<>();
        sessaoTreino.setItensExecutados(novaLista);
        assertSame(novaLista, sessaoTreino.getItensExecutados());
    }

    //testar adicionar item ao plano
    @Test
    @DisplayName("Teste do adicionar item ao SessaoTreino")
    void testAdicionarItem() {
        sessaoVazia.getItensExecutados().add(itemMock);
        assertEquals(1, sessaoVazia.getItensExecutados().size());
        assertSame(itemMock, sessaoVazia.getItensExecutados().getFirst());
    }

    //testar toString
    @Test
    @DisplayName("Teste do toString de SessaoTreino")
    void testToString() {
        StringBuilder expected = new StringBuilder();
        expected.append("ID Sessão: ").append(sessaoTreino.getIdSessao())
                .append(", ID Usuário: ").append(sessaoTreino.getIdUsuario())
                .append(", ID Plano: ").append(sessaoTreino.getIdPlanoTreino())
                .append(", Data: ").append(sessaoTreino.getDataSessao()).append("\n")
                .append("  Exercícios Registrados:\n")
                .append("    ").append(1).append(". ").append(itemMock.toString()).append("\n")
                .append("    ").append(2).append(". ").append(itemMock2.toString()).append("\n");
        assertEquals(expected.toString(), sessaoTreino.toString());
    }

    //testar toString com lista vazia
    @Test
    @DisplayName("Teste do toString de SessaoTreino com lista vazia")
    void testToStringEmptyList() {
        StringBuilder expected = new StringBuilder();
        expected.append("ID Sessão: ").append(sessaoVazia.getIdSessao())
                .append(", ID Usuário: ").append(sessaoVazia.getIdUsuario())
                .append(", ID Plano: ").append(sessaoVazia.getIdPlanoTreino())
                .append(", Data: ").append(sessaoVazia.getDataSessao()).append("\n")
                .append("  [Nenhum exercício registrado nesta sessão.]");
        assertEquals(expected.toString(), sessaoVazia.toString());
    }


}