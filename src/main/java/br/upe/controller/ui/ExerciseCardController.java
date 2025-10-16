package br.upe.controller.ui;

import br.upe.data.beans.Exercicio;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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
        // Lógica para carregar a imagem real do exercício
        // try {
        //     Image img = new Image(getClass().getResourceAsStream(exercicio.getCaminhoGif()));
        //     this.exerciseImage.setImage(img);
        // } catch (Exception e) {
        //     logger.warning("Não foi possível carregar a imagem para o exercício: " + exercicio.getNome());
        //     // A imagem placeholder definida no FXML será mantida
        // }
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