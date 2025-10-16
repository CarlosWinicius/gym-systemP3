package br.upe.controller.ui;

import br.upe.data.beans.PlanoTreino;
import javafx.fxml.FXML; // Importe a anotação!
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PlanoListController extends BaseController {

    @FXML
    private Label nomePlanoLabel;
    @FXML
    private HBox cardPane;

    private PlanoTreino planoData;
    private PlansScreenController parentController;

    public void setData(PlanoTreino plano, PlansScreenController parentController) {
        this.planoData = plano;
        this.parentController = parentController;
        this.nomePlanoLabel.setText(plano.getNome());
    }

    /**
     * Chamado quando o usuário clica na área principal do card para visualizá-lo.
     */
    @FXML // <--- CORREÇÃO AQUI!
    private void handleVerPlano(MouseEvent event) {
        // A lógica de ver e editar agora é a mesma: ir para a tela de edição.
        handleEditarPlano(event);
    }

    /**
     * Chamado quando o usuário clica no ícone de caneta para editar.
     */
    @FXML // <--- CORREÇÃO AQUI!
    private void handleEditarPlano(MouseEvent event) {
        if (planoData == null) return;

        logger.info("Navegando para a tela de edição do plano: " + planoData.getNome());

        EditPlanScreenController controller = navigateTo(cardPane, "/ui/EditPlanScreen.fxml", EditPlanScreenController.class);
        if (controller != null) {
            controller.initData(this.planoData);
        }
    }

    /**
     * Chamado quando o usuário clica no ícone de lixeira para deletar.
     */
    @FXML // <--- CORREÇÃO AQUI!
    public void handleDeletePlano(MouseEvent mouseEvent) {
        logger.info("Deletando o plano: " + planoData.getNome());
        // Lógica para chamar o service e deletar do banco
        // planoTreinoService.deletarPlano(usuarioLogado.getId(), planoData.getNome());

        parentController.removerCardDaTela(cardPane);
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano '" + planoData.getNome() + "' removido.");
    }
}