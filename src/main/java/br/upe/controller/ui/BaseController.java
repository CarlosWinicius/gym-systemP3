package br.upe.controller.ui;

import br.upe.data.beans.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseController {
    protected static final double APP_WIDTH = 900;
    protected static final double APP_HEIGHT = 600;

    protected static Usuario usuarioLogado;

    protected final Logger logger = Logger.getLogger(getClass().getName());


    protected void navigateTo(Node eventSource, String fxmlFile) {
        try {
            loadScene(eventSource, fxmlFile);
        } catch (IOException e) {
            handleNavigationError(e, fxmlFile);
        }
    }


    protected <T> T navigateTo(Node eventSource, String fxmlFile, Class<T> controllerClass) {
        try {
            FXMLLoader loader = loadScene(eventSource, fxmlFile);

            return loader.getController();
        } catch (IOException e) {
            handleNavigationError(e, fxmlFile);
            return null;
        }
    }

    private FXMLLoader loadScene(Node eventSource, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage stage = (Stage) eventSource.getScene().getWindow();
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();

        return loader;
    }

    private void handleNavigationError(IOException e, String fxmlFile) {
        logger.log(Level.SEVERE, "Erro de Navegação: Não foi possível carregar ''{0}: {1}", new Object[]{fxmlFile, e});
        showAlert(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela: " + fxmlFile);
    }

    protected void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}