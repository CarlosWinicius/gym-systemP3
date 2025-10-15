package br.upe.controller.ui;

import br.upe.data.beans.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.logging.Logger;

public abstract class BaseController {

    private static final Logger logger = Logger.getLogger(BaseController.class.getName());

    protected static final double APP_WIDTH = 900;
    protected static final double APP_HEIGHT = 600;

    protected static Usuario usuarioLogado;

    protected void navigateTo(Node eventSource, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) eventSource.getScene().getWindow();

            stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));

            stage.show();
        } catch (IOException e) {
            logger.severe("Erro ao carregar o arquivo FXML: " + fxmlFile + "." + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela: " + fxmlFile);
        }
    }

    protected void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}