
package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.beans.PlanoTreino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeScreenController extends BaseController implements Initializable {

    @FXML
    private FlowPane plansContainer;

    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();
    private Image dumbbellIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Inicializando HomeScreen e carregando planos do usuário.");
        if (plansContainer == null) {
            logger.severe("plansContainer não injetado no FXML.");
            return;
        }

        // carrega ícone (opcional)
        InputStream is = getClass().getResourceAsStream("/resources/images/dumbbell.png");
        if (is != null) {
            dumbbellIcon = new Image(is);
        } else {
            logger.warning("Ícone de haltere não encontrado em /resources/images/dumbbell.png");
        }

        carregarPlanosDoUsuario();
    }

    private void carregarPlanosDoUsuario() {
        plansContainer.getChildren().clear();

        if (usuarioLogado == null) {
            logger.warning("Usuário não logado — não é possível carregar planos na HomeScreen.");
            showAlert(Alert.AlertType.INFORMATION, "Nenhum usuário", "Faça login para ver seus planos.");
            return;
        }

        List<PlanoTreino> planos = planoTreinoService.listarMeusPlanos(usuarioLogado.getId());
        if (planos == null || planos.isEmpty()) {
            logger.info("Nenhum plano encontrado para o usuário ID: " + usuarioLogado.getId());
            // opcional: mostrar mensagem visual no UI
            return;
        }

        for (PlanoTreino plano : planos) {
            Button btn = criarBotaoParaPlano(plano);
            plansContainer.getChildren().add(btn);
        }
    }

    private Button criarBotaoParaPlano(PlanoTreino plano) {
        Button btn = new Button(plano.getNome());
        btn.setPrefSize(260, 64);
        btn.setContentDisplay(ContentDisplay.RIGHT);

        if (dumbbellIcon != null) {
            ImageView iv = new ImageView(dumbbellIcon);
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            btn.setGraphic(iv);
        }

        btn.setOnAction((ActionEvent e) -> {
            logger.info("Abrindo plano: " + plano.getNome() + " (ID: " + plano.getIdPlano() + ")");
            try {
                if (plano == null) return;

                logger.info("Navegando para a tela de edição do plano: " + plano.getNome());

                // Usa o botão (que gerou o evento) como referência de Node
                EditPlanScreenController controller = navigateTo(
                        (javafx.scene.Node) e.getSource(),
                        "/ui/EditPlanScreen.fxml",
                        EditPlanScreenController.class
                );

                // Chama o método correto do controller existente
                if (controller != null) {
                    controller.initData(plano);
                }

            } catch (Exception ex) {
                logger.severe("Erro ao tentar abrir tela de detalhe do plano: " + ex.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir o plano.");
            }
        });

        return btn;
    }
}
