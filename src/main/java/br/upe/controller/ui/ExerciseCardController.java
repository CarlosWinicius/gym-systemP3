package br.upe.controller.ui;

import br.upe.data.entities.Exercicio;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;

public class ExerciseCardController extends BaseController {

    @FXML private VBox cardPane;
    @FXML private ImageView exerciseImage;
    @FXML private Label exerciseName;
    @FXML private HBox botoesBox;

    private Exercicio exercicio;
    private ExerciseScreenController parentController;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        selected.addListener((obs, wasSelected, isSelected) ->
            cardPane.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), isSelected)
        );
    }

    public void setData(Exercicio exercicio) {
        setData(exercicio, null);
    }

    public void setData(Exercicio exercicio, ExerciseScreenController parentController) {
        this.exercicio = exercicio;
        this.parentController = parentController;
        this.exerciseName.setText(exercicio.getNome());


        try {
            String caminho = exercicio.getCaminhoGif();
            if (caminho == null || caminho.isBlank()) {
                throw new IOException("Caminho do GIF não fornecido.");
            }
            String fullPath = "/gif/" + new File(caminho).getName();
            Image img = new Image(getClass().getResourceAsStream(fullPath));
            if (img.isError()) {
                throw new IOException("Imagem não encontrada no caminho: " + fullPath);
            }
            this.exerciseImage.setImage(img);
        } catch (Exception e) {
            logger.warning("Não foi possível carregar imagem para o exercício: " + exercicio.getNome() + ". Erro: " + e.getMessage());
            Image placeholder = new Image(getClass().getResourceAsStream("/images/no image.png"));
            this.exerciseImage.setImage(placeholder);
        }

        if (this.parentController != null && usuarioLogado != null && exercicio.getUsuario().getId() == usuarioLogado.getId()) {
            botoesBox.setVisible(true);
            botoesBox.setManaged(true);
        } else {
            botoesBox.setVisible(false);
            botoesBox.setManaged(false);
        }


        cardPane.setOnMouseClicked(event -> {
            if (this.parentController != null) {
                parentController.handleVisualizarExercicio(this.exercicio);
            } else {
                toggleSelection();
            }
        });
    }

    @FXML
    void handleEditar(ActionEvent event) {
        event.consume();
        if (parentController != null) {
            parentController.handleEditarExercicio(this.exercicio);
        }
    }

    @FXML
    void handleExcluir(ActionEvent event) {
        event.consume();
        if (parentController != null) {
            parentController.handleExcluirExercicio(this.exercicio);
        }
    }

    public Exercicio getExercicio() { return this.exercicio; }
    public void toggleSelection() { selected.set(!selected.get()); }
    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean isSelected) { selected.set(isSelected); }
    public VBox getCardPane() { return cardPane; }
}