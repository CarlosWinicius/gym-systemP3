package br.upe.controller.ui;

import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.UsuarioService;
import br.upe.data.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserAdminScreenController extends BaseController {

    @FXML
    private VBox userListVBox;

    private final IUsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        userListVBox.getChildren().clear();
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();

        for (Usuario user : usuarios) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/UserAdminRow.fxml"));
                HBox userRowNode = loader.load();

                UserAdminRowController rowController = loader.getController();
                rowController.setData(user, this);

                userListVBox.getChildren().add(userRowNode);
            } catch (IOException e) {
                logger.severe("Falha ao carregar o componente UserAdminRow.fxml: " + e.getMessage());
            }
        }
    }

    public void handleDeleteUser(Usuario usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir: " + usuario.getNome());
        alert.setContentText("Você tem certeza que deseja excluir este usuário?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (usuarioLogado != null && usuarioLogado.getId() == usuario.getId()) {
                    showAlert(Alert.AlertType.ERROR, "Ação Inválida", "Você não pode excluir sua própria conta.");
                    return;
                }

                usuarioService.removerUsuario(usuario.getId());

                loadUsers();

                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário removido com sucesso!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao Excluir", "Não foi possível excluir o usuário: " + e.getMessage());
            }
        }
    }
}