package br.upe.controller.ui;

import br.upe.data.beans.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Logger; // Import do Logger

public abstract class BaseController {
    protected static final double APP_WIDTH = 900;
    protected static final double APP_HEIGHT = 600;

    protected static Usuario usuarioLogado;

    // Logger para ser usado pelas classes filhas
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Navega para uma nova tela (versão simples, "fire-and-forget").
     */
    protected void navigateTo(Node eventSource, String fxmlFile) {
        try {
            // Internamente, agora ele usa o método mais poderoso
            loadScene(eventSource, fxmlFile);
        } catch (IOException e) {
            handleNavigationError(e, fxmlFile);
        }
    }

    /**
     * Navega para uma nova tela e retorna o controller dela.
     * Perfeito para quando precisamos passar dados para a próxima tela.
     * @param <T> O tipo do controller que esperamos receber.
     * @return A instância do controller da nova cena.
     */
    protected <T> T navigateTo(Node eventSource, String fxmlFile, Class<T> controllerClass) {
        try {
            FXMLLoader loader = loadScene(eventSource, fxmlFile);
            // Retorna o controller para que o método chamador possa usá-lo
            return loader.getController();
        } catch (IOException e) {
            handleNavigationError(e, fxmlFile);
            return null; // Retorna null em caso de erro
        }
    }

    /**
     * Lógica centralizada para carregar e exibir uma cena.
     * @return O FXMLLoader usado, para acesso ao controller.
     */
    private FXMLLoader loadScene(Node eventSource, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage stage = (Stage) eventSource.getScene().getWindow();
        stage.setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        stage.show();

        return loader;
    }

    private void handleNavigationError(IOException e, String fxmlFile) {
        logger.log(java.util.logging.Level.SEVERE, "Erro de Navegação: Não foi possível carregar " + fxmlFile, e);
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