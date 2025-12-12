package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.entities.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;

public class SignInScreenController extends BaseController {

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

        toggleLoading(true);

        javafx.concurrent.Task<Usuario> cadastroTask = new javafx.concurrent.Task<>() {
            @Override
            protected Usuario call() throws Exception {
                return usuarioService.cadastrarUsuario(nome, email, senha, TipoUsuario.COMUM);
            }
        };

        cadastroTask.setOnSucceeded(event -> {
            toggleLoading(false);
            Usuario usuario = cadastroTask.getValue();

            if (usuario != null) {
                BaseController.usuarioLogado = usuario;
                navigateTo(signInButton, "/ui/HomeScreen.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, "Não foi possível completar o cadastro. Tente novamente.");
            }
        });

        cadastroTask.setOnFailed(event -> {
            toggleLoading(false);
            Throwable erro = cadastroTask.getException();
            showAlert(Alert.AlertType.ERROR, ERRO_CADASTRO_TITULO, erro.getMessage());
        });

        new Thread(cadastroTask).start();
    }

    private void toggleLoading(boolean isLoading) {
        signInButton.setDisable(isLoading);
        signInButton.setText(isLoading ? "Cadastrando..." : "Cadastrar");

        nameField.setDisable(isLoading);
        emailField.setDisable(isLoading);
        passwordField.setDisable(isLoading);
        confirmPasswordField.setDisable(isLoading);
    }

    @FXML
    private void handleGoToLogin(MouseEvent event) {
        logger.info("Navegando para a tela de Login...");
        navigateTo((Node) event.getSource(), "/ui/LoginScreen.fxml");
    }
}