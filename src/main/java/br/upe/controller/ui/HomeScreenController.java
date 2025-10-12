package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeScreenController extends BaseController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private SideMenuController sideMenuController;

    @FXML
    public void initialize() {

        if (usuarioLogado != null) {
            welcomeLabel.setText("Bem-vindo, " + usuarioLogado.getNome() + "!");
        }
    }
}