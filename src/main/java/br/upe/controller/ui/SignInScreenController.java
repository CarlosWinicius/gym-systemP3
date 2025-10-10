package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import br.upe.utils.SceneLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class SignInScreenController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button SignInButton;

    @FXML
    private Label LoginAccountLabel;

    @FXML
    private Label errorMessage;

    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> emailField.requestFocus());

        // adiciona eventos aos botões e labels
        SignInButton.setOnAction(e -> handleSignIn());
//        LoginAccountLabel.setOnMouseClicked(this::handleCreateAccount);

    }
    private void handleSignIn() {
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();

        try {
            Usuario usuario = usuarioService.cadastrarUsuario(nome,email,senha, TipoUsuario.COMUM);

            if (usuario != null) {
                // Aqui você pode salvar o usuário em uma sessão
                // e trocar de tela, por exemplo:
                System.out.println("Cadastro bem-sucedido! Usuário: " + usuario.getNome());
                // SceneLoader.loadScene("/org/upe/ui/telaInicio.fxml", "Home", rightPane);
            } else {
                showError("Credenciais inválidas. Tente novamente.");
            }
        } catch (IllegalArgumentException e) {
            showError("Erro ao cadastrar: " + e.getMessage());
        } catch (Exception e) {
            showError("Erro inesperado: " + e.getMessage());
        }
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
