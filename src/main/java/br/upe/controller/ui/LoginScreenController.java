package br.upe.controller.ui;

import br.upe.controller.business.UsuarioService;
import br.upe.data.entities.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginScreenController extends BaseController {

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

        loginButton.setDisable(true);
        loginButton.setText("Entrando...");

        javafx.concurrent.Task<Usuario> loginTask = new javafx.concurrent.Task<>() {
            @Override
            protected Usuario call() throws Exception {
                return usuarioService.autenticarUsuario(email, senha);
            }
        };

        loginTask.setOnSucceeded(event -> {
            Usuario usuario = loginTask.getValue();
            if (usuario != null) {
                BaseController.usuarioLogado = usuario;
                navigateTo(loginButton, "/ui/HomeScreen.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Login", "Credenciais inválidas. Tente novamente.");
                resetLoginButton();
            }
        });

        loginTask.setOnFailed(event -> {
            Throwable erro = loginTask.getException();
            logger.log(java.util.logging.Level.SEVERE, "Erro no login", erro);
            showAlert(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível conectar ao servidor: " + erro.getMessage());
            resetLoginButton();
        });

        new Thread(loginTask).start();
    }

    private void resetLoginButton() {
        loginButton.setDisable(false);
        loginButton.setText("Entrar");
    }

    @FXML
    private void handleGoToSignIn(MouseEvent event) {
        logger.info("Navegando para a tela de cadastro...");
        navigateTo(createAccountLabel, "/ui/SignInScreen.fxml");
    }
}
