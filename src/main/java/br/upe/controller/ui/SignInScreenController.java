package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import java.util.logging.Logger;

public class SignInScreenController extends BaseController {

    private static final Logger logger = Logger.getLogger(SignInScreenController.class.getName());

    private static final String ERRO_CADASTRO_TITULO = "Erro de Cadastro";

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button signInButton;

    @FXML
    private Label loginAccountLabel;

    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {

        Platform.runLater(() -> nameField.requestFocus());
        signInButton.setOnAction(e -> handleSignIn());

    }

    @FXML
    private void handleSignIn() {
        String nome = nameField.getText();
        String email = emailField.getText();
        String senha = passwordField.getText();
        String confirmarSenha = confirmPasswordField.getText();

        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, "Todos os campos são obrigatórios.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, "As senhas não coincidem. Tente novamente.");
            return;
        }

        try {
            Usuario usuario = usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);

            if (usuario != null) {

                showAlert(Alert.AlertType.INFORMATION, "Cadastro Realizado", "Usuário " + usuario.getNome() + " cadastrado com sucesso!");

                BaseController.usuarioLogado = usuario;

                navigateTo(signInButton, "/ui/HomeScreen.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, "Não foi possível completar o cadastro. Tente novamente.");
            }
        } catch (Exception e) {

            showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, "Ocorreu um erro: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToLogin(MouseEvent event) {
        logger.info("Navegando para a tela de Login...");
        navigateTo((Node) event.getSource(), "/ui/LoginScreen.fxml");
    }
}