package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class SignInScreenController extends BaseController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField; // Correto: Usar PasswordField para senhas

    @FXML
    private TextField confirmPasswordField; // Correto: Usar PasswordField para senhas

    @FXML
    private Button signInButton;

    @FXML
    private Label loginAccountLabel;

    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        // Coloca o foco no campo de nome quando a tela abre
        Platform.runLater(() -> nameField.requestFocus());

        // Associa a ação de clique do botão ao método handleSignIn
        signInButton.setOnAction(e -> handleSignIn());

    }

    @FXML
    private void handleSignIn() {
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();
        String confirmarSenha = confirmPasswordField.getText();

        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Todos os campos são obrigatórios.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "As senhas não coincidem. Tente novamente.");
            return;
        }

        try {
            Usuario usuario = usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);

            if (usuario != null) {

                showAlert(Alert.AlertType.INFORMATION, "Cadastro Realizado", "Usuário " + usuario.getNome() + " cadastrado com sucesso!");

                BaseController.usuarioLogado = usuario;

                navigateTo(signInButton, "/ui/HomeScreen.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Não foi possível completar o cadastro. Tente novamente.");
            }
        } catch (Exception e) {

            showAlert(Alert.AlertType.ERROR, "Erro de Cadastro", "Ocorreu um erro: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToLogin(MouseEvent event) {
        System.out.println("Navegando para a tela de Login...");
        navigateTo(loginAccountLabel, "/ui/LoginScreen.fxml");
    }
}