package br.upe.controller.ui;

import br.upe.controller.business.UsuarioService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        UsuarioService usuarioService = new UsuarioService();
        usuarioService.verificarECriarAdminPadrao();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/LoginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setTitle("SYSFIT");

        InputStream iconStream = getClass().getResourceAsStream("/images/halter.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        }

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}