package br.upe.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneLoader {

    private SceneLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void loadScene(String path, String title, Pane currentPage) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(path));
            Parent root = loader.load();

            Stage stage = (Stage) currentPage.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
