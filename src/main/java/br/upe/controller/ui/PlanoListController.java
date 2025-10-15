package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Controller para um único card de plano (PlanoList.fxml).
 * Gerencia os dados e eventos de um item da lista.
 */
public class PlanoListController {

    @FXML
    private Label nomePlanoLabel; // Injeta o Label com fx:id="nomePlanoLabel"

    // Variável para guardar os dados do plano (pode ser o nome, ID, ou um objeto completo)
    private Object planoData;

    /**
     * Método público para configurar o card com dados vindos de fora.
     * Este é o "portal de entrada" de informações para o nosso componente.
     * @param nomeDoPlano O texto que aparecerá no card.
     */
    public void setData(String nomeDoPlano) {
        this.nomePlanoLabel.setText(nomeDoPlano);
        this.planoData = nomeDoPlano; // Por enquanto, o dado é o próprio nome
    }

    /**
     * Chamado quando o usuário clica na área principal do card para visualizá-lo.
     */
    @FXML
    private void handleVerPlano(MouseEvent event) {
        System.out.println("Clicou para VER o plano: " + planoData);
        // Futuramente, a lógica de navegação virá aqui.
    }

    /**
     * Chamado quando o usuário clica no ícone de caneta para editar.
     */
    @FXML
    private void handleEditarPlano(MouseEvent event) {
        System.out.println("Clicou para EDITAR o plano: " + planoData);
        // Futuramente, a lógica para abrir a tela de edição virá aqui.
    }

    public void handleDeletePlano(MouseEvent mouseEvent) {
    }
}