package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.PlanoTreino;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class PlanoListController extends BaseController {

    @FXML
    private Label nomePlanoLabel;
    @FXML
    private HBox cardPane;

    private PlanoTreino planoData;
    private PlansScreenController parentController;
    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();

    public void setData(PlanoTreino plano, PlansScreenController parentController) {
        this.planoData = plano;
        this.parentController = parentController;
        this.nomePlanoLabel.setText(plano.getNome());
    }

    @FXML
    private void handleVerPlano(MouseEvent event) {
        handleEditarPlano(event);
    }

    @FXML
    private void handleEditarPlano(MouseEvent event) {
        if (planoData == null) return;

        logger.info("Navegando para a tela de edição do plano: " + planoData.getNome());

        EditPlanScreenController controller = navigateTo(cardPane, "/ui/EditPlanScreen.fxml", EditPlanScreenController.class);
        if (controller != null) {
            controller.initData(this.planoData);
        }
    }

    @FXML
    public void handleDeletePlano(MouseEvent mouseEvent) {
        logger.info("Deletando o plano: " + planoData.getNome());
        planoTreinoService.deletarPlano(usuarioLogado.getId(), planoData.getNome());
        parentController.removerCardDaTela(cardPane);
        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano '" + planoData.getNome() + "' removido.");
    }
}