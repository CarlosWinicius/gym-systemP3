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

// 1. A classe agora herda de BaseController para usar os métodos navigateTo e showAlert.
public class LoginScreenController extends BaseController {

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

    // A lógica de negócio para autenticar o usuário.
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> emailField.requestFocus());
        loginButton.setOnAction(e -> handleLogin());
        createAccountLabel.setOnMouseClicked(this::handleCreateAccount);

        // Adiciona um listener para que a tecla ENTER no campo de senha também acione o login
        passwordField.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String email = emailField.getText();
        String senha = passwordField.getText();

        try {
            Usuario usuario = usuarioService.autenticarUsuario(email, senha);

            // 2. Se o usuário for encontrado e a senha estiver correta...
            if (usuario != null) {
                // 3. ...guardamos o usuário na variável estática da nossa classe base.
                // Agora, qualquer outro controller que herde de BaseController terá acesso a ele.
                BaseController.usuarioLogado = usuario;

                System.out.println("Login bem-sucedido! Usuário: " + usuario.getNome());

                // 4. Usamos o método herdado para navegar para a tela principal!
                // Passamos o botão de login como referência para obter a janela atual.
                navigateTo(loginButton, "/ui/HomeScreen.fxml");

            } else {
                // 5. Se as credenciais estiverem erradas, usamos o método herdado para mostrar um alerta.
                showAlert(Alert.AlertType.ERROR, "Erro de Login", "Credenciais inválidas. Tente novamente.");
            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Erro de Validação", "Erro: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCreateAccount(MouseEvent e) {
        System.out.println("Ir para tela de cadastro...");
        // Quando você criar a tela de cadastro, a navegação será feita aqui:
        // navigateTo(createAccountLabel, "/ui/CadastroScreen.fxml");
    }
}