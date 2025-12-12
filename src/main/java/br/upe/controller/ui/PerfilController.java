package br.upe.controller.ui;

import br.upe.controller.business.CalculadoraIMC;
import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.data.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;

public class PerfilController extends BaseController {

    @FXML private TextField nome;
    @FXML private TextField altura;
    @FXML private TextField peso;
    @FXML private TextField percentualGordura;
    @FXML private TextField percentualMM;
    @FXML private TextField imc;
    @FXML private Label categoriaIMC;
    @FXML private ImageView foto;

    private final IndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        carregarDadosNaTela();
    }

    @FXML
    protected void initialize() {
        // Não faz nada pesado aqui, porque o usuarioLogado pode ainda não estar setado.
    }

    private void carregarDadosNaTela() {
        if (usuarioLogado == null) return;

        // Campos sempre só leitura nesta tela
        nome.setMouseTransparent(true);
        nome.setFocusTraversable(false);

        altura.setMouseTransparent(true);
        altura.setFocusTraversable(false);

        peso.setMouseTransparent(true);
        peso.setFocusTraversable(false);

        percentualGordura.setMouseTransparent(true);
        percentualGordura.setFocusTraversable(false);

        percentualMM.setMouseTransparent(true);
        percentualMM.setFocusTraversable(false);

        imc.setMouseTransparent(true);
        imc.setFocusTraversable(false);

        nome.setText(usuarioLogado.getNome());

        if (usuarioLogado.getFotoPerfil() != null) {
            foto.setImage(new Image(new ByteArrayInputStream(usuarioLogado.getFotoPerfil())));
        } else {
            foto.setImage(null);
        }

        try {
            var lista = indicadorService.listarTodosDoUsuario(usuarioLogado.getId());
            if (!lista.isEmpty()) {
                var ultimo = lista.get(lista.size() - 1);
                altura.setText(String.valueOf(ultimo.getAlturaCm()));
                peso.setText(String.valueOf(ultimo.getPesoKg()));
                percentualGordura.setText(String.valueOf(ultimo.getPercentualGordura()));
                percentualMM.setText(String.valueOf(ultimo.getPercentualMassaMagra()));
                imc.setText(String.format("%.2f", ultimo.getImc()));
                categoriaIMC.setText(CalculadoraIMC.classificarImc(ultimo.getImc()));
            } else {
                altura.setText("");
                peso.setText("");
                percentualGordura.setText("");
                percentualMM.setText("");
                imc.setText("");
                categoriaIMC.setText("");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao carregar indicadores do usuário", e);
        }
        if (usuarioLogado != null && usuarioLogado.getFotoPerfil() != null) {
            foto.setImage(new Image(new ByteArrayInputStream(usuarioLogado.getFotoPerfil())));
        }
    }

    @FXML
    private void editarTela(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PerfilEditScreen.fxml"));
            Parent root = loader.load();

            PerfilEditController controller = loader.getController();
            controller.setUsuarioLogado(usuarioLogado);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao abrir tela de edição", e);
        }
    }


}
