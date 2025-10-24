package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.TipoUsuario;
import br.upe.data.beans.Usuario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UserAdminRowController extends BaseController {

    @FXML
    private HBox rootPane;
    @FXML
    private Label nameLabel;
    @FXML
    private ComboBox<TipoUsuario> userTypeComboBox;
    @FXML
    private Button deleteButton;

    private Usuario usuario;
    private UserAdminScreenController adminScreenController;
    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        userTypeComboBox.setItems(FXCollections.observableArrayList(TipoUsuario.values()));
        userTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && usuario != null && usuario.getTipo() != newVal) {
                updateUserRole(newVal);
            }
        });
    }

    public void setData(Usuario usuario, UserAdminScreenController adminScreenController) {
        this.usuario = usuario;
        this.adminScreenController = adminScreenController;
        nameLabel.setText(usuario.getNome());
        userTypeComboBox.setValue(usuario.getTipo());

        if (usuario.getId() == 1) {
            deleteButton.setDisable(true);
            userTypeComboBox.setDisable(true);
        }
    }

    private void updateUserRole(TipoUsuario newRole) {
        try {
            usuarioService.atualizarUsuario(usuario.getId(), null, null, null, newRole);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Permissão do usuário " + usuario.getNome() + " atualizada.");
            usuario.setTipo(newRole);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível alterar a permissão: " + e.getMessage());
            userTypeComboBox.setValue(usuario.getTipo());
        }
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        adminScreenController.handleDeleteUser(usuario);
    }
}