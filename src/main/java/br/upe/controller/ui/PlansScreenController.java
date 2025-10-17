package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.beans.PlanoTreino;
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

/**
 * Controller para a tela de listagem de planos (PlansScreen.fxml).
 * Carrega os planos existentes do usuário e gerencia a criação de novos.
 */
public class PlansScreenController extends BaseController {

    @FXML
    private TilePane PlanoTilePane;

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

        PlanoTilePane.getChildren().clear();
        logger.info("Buscando planos no banco de dados para o usuário ID: " + usuarioLogado.getId());


        List<PlanoTreino> planosDoUsuario = planoTreinoService.listarMeusPlanos(usuarioLogado.getId());

        logger.info("Foram encontrados " + planosDoUsuario.size() + " planos.");

        for (PlanoTreino plano : planosDoUsuario) {
            try {
                criarEAdicionarCard(plano);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Falha ao criar o card visual para o plano: " + plano.getNome(), e);
            }
        }
    }

    @FXML
    private void handleAdicionarPlano(ActionEvent event) {
        logger.info("Botão 'Adicionar Plano' pressionado.");
        PlanoTreino novoPlano = new PlanoTreino(usuarioLogado.getId(), "Novo Plano de Treino");

        EditPlanScreenController controller = navigateTo((Node) event.getSource(), "/ui/EditPlanScreen.fxml", EditPlanScreenController.class);
        if (controller != null) {
            controller.initData(novoPlano);
        }
    }

    public void removerCardDaTela(Node cardNode) {
        PlanoTilePane.getChildren().remove(cardNode);
    }

    private void criarEAdicionarCard(PlanoTreino plano) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PlanoList.fxml"));
        HBox cardNode = loader.load();

        PlanoListController cardController = loader.getController();
        // Passa o objeto 'plano' e a referência deste controller para o card filho
        cardController.setData(plano, this);

        PlanoTilePane.getChildren().add(cardNode);
    }
}