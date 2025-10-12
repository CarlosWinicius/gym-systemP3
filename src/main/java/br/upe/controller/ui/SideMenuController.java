package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class SideMenuController extends BaseController {

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
    private void handleNavigation(MouseEvent event) {
        String sourceId = ((Node) event.getSource()).getId();
        String fxmlFile = "";

        switch (sourceId) {
            case "homeButton":
                fxmlFile = "/ui/HomeScreen.fxml";
                break;
            case "perfilButton":
                fxmlFile = "/ui/PerfilScreen.fxml";
                break;
            case "planosButton":
                fxmlFile = "/ui/PlanosScreen.fxml";
                break;
            case "exerciciosButton":
                fxmlFile = "/ui/ExerciciosScreen.fxml";
                break;
            case "metricasButton":
                fxmlFile = "/ui/MetricasScreen.fxml";
                break;
            default:
                System.out.println("Nenhuma ação de navegação definida para o ID: " + sourceId);
                break;
        }

        if (!fxmlFile.isEmpty()) {
            navigateTo((Node) event.getSource(), fxmlFile);
        }
    }
}