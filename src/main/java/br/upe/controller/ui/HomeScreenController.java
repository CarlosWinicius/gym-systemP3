package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

// 1. Faça a classe herdar de BaseController.
public class HomeScreenController extends BaseController {

    // 2. Crie a variável para o Label de boas-vindas (dê o fx:id="welcomeLabel" no FXML).
    @FXML
    private Label welcomeLabel;

    // 3. (Opcional, mas recomendado) Crie uma variável para o controller do menu.
    // O nome DEVE ser o fx:id do <fx:include> + "Controller".
    @FXML
    private SideMenuController sideMenuController;

    /**
     * O método initialize() é chamado automaticamente depois que o FXML é carregado.
     * É o lugar perfeito para configurar a tela.
     */
    @FXML
    public void initialize() {
        // 4. Pega o usuário logado (que está guardado no BaseController) e personaliza a mensagem.
        if (usuarioLogado != null) {
            welcomeLabel.setText("Bem-vindo, " + usuarioLogado.getNome() + "!");
        }
    }
}