package br.upe.controller.ui;

import br.upe.data.beans.Exercicio;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

// Herdar de BaseController é uma boa prática para ter acesso ao logger e outros utilitários.
public class ExerciseCardController extends BaseController {

    @FXML
    private VBox cardPane;
    @FXML
    private ImageView exerciseImage;
    @FXML
    private Label exerciseName;

    private Exercicio exercicio;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        // Liga o estado 'selected' à pseudo-classe CSS ':selected' para o efeito de "aura"
        selected.addListener((obs, wasSelected, isSelected) -> {
            cardPane.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), isSelected);
        });
    }

    public void setData(Exercicio exercicio) {
        this.exercicio = exercicio;
        this.exerciseName.setText(exercicio.getNome());

         try {
             String caminho = exercicio.getCaminhoGif();
             if (caminho.contains("resources/")) {
                 caminho = caminho.substring(caminho.indexOf("resources/") + "resources/".length());
             }
             if (!caminho.startsWith("/")) {
                 caminho = "/" + caminho;
             }
             Image img = new Image(getClass().getResourceAsStream(caminho));
             this.exerciseImage.setImage(img);
         } catch (Exception e) {
             logger.warning("Não foi possível carregar a imagem para o exercício: " + exercicio.getNome() + exercicio.getCaminhoGif());
         }
    }
    public Exercicio getExercicio() {
        return this.exercicio;
    }
    public void toggleSelection() {
        selected.set(!selected.get());
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean isSelected) {
        selected.set(isSelected);
    }

    public VBox getCardPane() {
        return cardPane;
    }
}