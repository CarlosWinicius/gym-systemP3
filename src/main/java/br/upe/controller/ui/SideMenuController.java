package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SideMenuController extends BaseController {

    private static final Logger logger = Logger.getLogger(SideMenuController.class.getName());

    @FXML
    private Label homeButton;
    @FXML
    private Label perfilButton;
    @FXML
    private Label planosButton;
    @FXML
    private Label exerciciosButton;
    @FXML
    private Label metricasButton;
    @FXML
    private Label logoutButton;

    @FXML
    private void handleNavigation(MouseEvent event) {
        Node source = (Node) event.getSource();
        String sourceId = source.getId();
        String fxmlFile = "";

        switch (sourceId) {
            case "homeButton":
                fxmlFile = "/ui/HomeScreen.fxml";
                break;
            case "perfilButton":
                // fxmlFile = "/ui/PerfilScreen.fxml"; // Descomente quando criar a tela
                break;
            case "planosButton":
                fxmlFile = "/ui/PlansScreen.fxml";
                break;
            case "exerciciosButton":
                fxmlFile = "/ui/ExerciseScreen.fxml";
                break;
            case "metricasButton":
                // fxmlFile = "/ui/MetricasScreen.fxml"; // Descomente quando criar a tela
                break;
            default:
                logger.info("Nenhuma ação de navegação definida para o ID: " + sourceId);
                return;
        }

        if (!fxmlFile.isEmpty()) {
            navigateTo(source, fxmlFile);
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Efetuando logout e retornando à tela de login.");
        }
        BaseController.usuarioLogado = null;
        navigateTo((Node) event.getSource(), "/ui/LoginScreen.fxml");
    }
}
