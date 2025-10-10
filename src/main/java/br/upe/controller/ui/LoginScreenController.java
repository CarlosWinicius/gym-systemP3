package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.beans.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class LoginScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label createAccountLabel;

    @FXML
    private Label errorMessage;

    @FXML
    private VBox rightPane; // ou o AnchorPane principal, dependendo do seu FXML

    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> emailField.requestFocus());

        // adiciona eventos aos botões e labels
        loginButton.setOnAction(e -> handleLogin());
        createAccountLabel.setOnMouseClicked(this::handleCreateAccount);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String senha = passwordField.getText();

        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);

            if (usuario != null) {
                // Aqui você pode salvar o usuário em uma sessão
                // e trocar de tela, por exemplo:
                System.out.println("Login bem-sucedido! Usuário: " + usuario.getNome());
                // SceneLoader.loadScene("/org/upe/ui/telaInicio.fxml", "Home", rightPane);
            } else {
                showError("Credenciais inválidas. Tente novamente.");
            }
        } catch (IllegalArgumentException e) {
            showError("Erro: " + e.getMessage());
        } catch (Exception e) {
            showError("Erro inesperado: " + e.getMessage());
        }
    }

    private void handleCreateAccount(MouseEvent e) {
        // trocar tela para cadastro
        System.out.println("Ir para tela de cadastro...");
        // SceneLoader.loadScene("/org/upe/ui/telaCadastro.fxml", "Cadastro", rightPane);
    }

    private void showError(String message) {
        if (errorMessage != null) {
            errorMessage.setText(message);
            errorMessage.setVisible(true);
        } else {
            System.err.println(message);
        }
    }
}
