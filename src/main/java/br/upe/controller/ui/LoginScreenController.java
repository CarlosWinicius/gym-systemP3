package br.upe.controller.ui;

import br.upe.controller.business.UsuarioService;
import br.upe.data.beans.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.logging.Logger;

public class LoginScreenController extends BaseController {

    private static final Logger logger = Logger.getLogger(LoginScreenController.class.getName());

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label createAccountLabel;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> emailField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String senha = passwordField.getText();
        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);
            if (usuario != null) {
                BaseController.usuarioLogado = usuario;
                navigateTo(loginButton, "/ui/HomeScreen.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Login", "Credenciais inválidas. Tente novamente.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToSignIn(MouseEvent event) {
        logger.info("Navegando para a tela de cadastro...");
        navigateTo(createAccountLabel, "/ui/SignInScreen.fxml");
    }
}