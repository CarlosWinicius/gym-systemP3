package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.PlanoTreino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeScreenController extends BaseController implements Initializable {

    @FXML
    private FlowPane plansContainer;

    @FXML
    private Label welcomeLabel;

    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();
    private Image gymIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (usuarioLogado != null) {
            String nomeExibicao = usuarioLogado.getNome();
            welcomeLabel.setText("Bem vindo, " + nomeExibicao + "!");
        }

        gymIcon = carregarIcone();
        carregarPlanosDoUsuario();
    }

    private Image carregarIcone() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/healthicons_gym.png");
            if (is == null) {
                is = getClass().getResourceAsStream("/images/cuida_weight-outline.png");
            }
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            logger.warning("Erro ao carregar ícone: " + e.getMessage());
        }
        return null;
    }

    private void carregarPlanosDoUsuario() {
        plansContainer.getChildren().clear();

        if (usuarioLogado == null) return;

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(usuarioLogado.getId());

        if (planos != null) {
            for (PlanoTreino plano : planos) {
                Button btn = criarCardPersonalizado(plano);
                plansContainer.getChildren().add(btn);
            }
        }
    }

    private Button criarCardPersonalizado(PlanoTreino plano) {
        Button btn = new Button(plano.getNome());

        btn.getStyleClass().add("card-plano");

        if (gymIcon != null) {
            ImageView iv = new ImageView(gymIcon);
            iv.setFitWidth(30);
            iv.setFitHeight(30);
            iv.setPreserveRatio(true);

            btn.setGraphic(iv);
            btn.setContentDisplay(ContentDisplay.LEFT);
            btn.setGraphicTextGap(15);
        }

        btn.setOnAction((ActionEvent e) -> {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseHome.fxml"));
                Parent root = loader.load();

                ExerciseHomeController controller = loader.getController();

                Stage stage = new Stage();
                stage.setTitle("Treino: " + plano.getNome());
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.initModality(Modality.WINDOW_MODAL);

                if (plansContainer.getScene() != null) {
                    stage.initOwner(plansContainer.getScene().getWindow());
                }

                controller.setDialogStage(stage);

                if (plano.getItensTreino() != null && !plano.getItensTreino().isEmpty()) {
                    controller.setItensDoPlano(plano.getItensTreino());
                    stage.showAndWait();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Plano Vazio", "Adicione exercícios a este plano antes de iniciar.");
                }

            } catch (IOException ex) {
                logger.severe("Erro ao abrir treino: " + ex.getMessage());
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao abrir o treino.");
            }
        });

        return btn;
    }
}