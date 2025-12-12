package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.PlanoTreino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class PlansScreenController extends BaseController {

    @FXML
    private TilePane planoTilePane;

    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();

    @FXML
    public void initialize() {
        logger.info("Inicializando a tela 'Meus Planos'.");
        carregarPlanosDoUsuario();
    }

    private void carregarPlanosDoUsuario() {
        if (usuarioLogado == null) {
            logger.severe("Nenhum usuário logado! Não é possível carregar os planos.");
            showAlert(Alert.AlertType.ERROR, "Erro Crítico", "Usuário não identificado. Faça o login novamente.");
            return;
        }

        planoTilePane.getChildren().clear();
        logger.info("Buscando planos no banco de dados para o usuário ID: " + usuarioLogado.getId());


        List<PlanoTreino> planosDoUsuario = planoTreinoService.listarMeusPlanos(usuarioLogado.getId());

        logger.log(Level.INFO, "Foram encontrados {0} planos.", planosDoUsuario.size());

        for (PlanoTreino plano : planosDoUsuario) {
            try {
                criarEAdicionarCard(plano);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Falha ao criar o card visual para o plano ''{0}'': {1}", new Object[]{plano.getNome(), e});
            }
        }
    }

    @FXML
    private void handleAdicionarPlano(ActionEvent event) {
        logger.info("Botão 'Adicionar Plano' pressionado.");

        PlanoTreino novoPlano = new PlanoTreino();
        novoPlano.setNome("Novo Plano de Treino");

        EditPlanScreenController controller = navigateTo((Node) event.getSource(), "/ui/EditPlanScreen.fxml", EditPlanScreenController.class);
        if (controller != null) {
            controller.initData(novoPlano);
        }
    }

    public void removerCardDaTela(Node cardNode) {
        planoTilePane.getChildren().remove(cardNode);
    }

    private void criarEAdicionarCard(PlanoTreino plano) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PlanoList.fxml"));
        HBox cardNode = loader.load();

        PlanoListController cardController = loader.getController();
        cardController.setData(plano, this);

        planoTilePane.getChildren().add(cardNode);
    }
}