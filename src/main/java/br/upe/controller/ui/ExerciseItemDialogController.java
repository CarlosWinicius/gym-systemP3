package br.upe.controller.ui;

import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class ExerciseItemDialogController {
    @FXML private ImageView exerciseImage;
    @FXML private Label exerciseName;
    @FXML private TextField cargaField;
    @FXML private TextField repsField;
    @FXML private Button btnSalvar;
    @FXML private Button btnRemover;

    private Stage dialogStage;
    private ItemPlanoTreino itemResultado;
    private Exercicio exercicioBase;
    private boolean confirmado = false;
    private boolean remover = false;

    @FXML
    public void initialize() {
        repsField.setEditable(false);
        cargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cargaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDados(Exercicio exercicio, ItemPlanoTreino itemExistente) {
        this.exercicioBase = exercicio;
        this.exerciseName.setText(exercicio.getNome());

        try {
            String caminho = exercicio.getCaminhoGif();
            if (caminho != null && !caminho.isBlank()) {
                String fullPath = "/gif/" + new File(caminho).getName();
                var url = getClass().getResource(fullPath);
                if (url != null) {
                    exerciseImage.setImage(new Image(url.toString()));
                } else {
                    carregarImagemPadrao();
                }
            } else {
                carregarImagemPadrao();
            }
        } catch (Exception e) {
            carregarImagemPadrao();
        }

        if (itemExistente != null) {
            cargaField.setText(String.valueOf(itemExistente.getCargaKg()));
            repsField.setText(String.valueOf(itemExistente.getRepeticoes()));
            btnSalvar.setText("Salvar");
            btnRemover.setVisible(true);
        } else {
            cargaField.setText("0");
            repsField.setText("0");
            btnSalvar.setText("Adicionar");
            btnRemover.setVisible(false);
        }
    }

    private void carregarImagemPadrao() {
        try {
            exerciseImage.setImage(new Image(getClass().getResourceAsStream("/images/no image.png")));
        } catch (Exception ignored) {}
    }

    @FXML
    private void incrementReps() {
        try {
            int val = Integer.parseInt(repsField.getText());
            repsField.setText(String.valueOf(val + 1));
        } catch (NumberFormatException e) {
            repsField.setText("1");
        }
    }

    @FXML
    private void decrementReps() {
        try {
            int val = Integer.parseInt(repsField.getText());
            if (val > 0) repsField.setText(String.valueOf(val - 1));
        } catch (NumberFormatException e) {
            repsField.setText("0");
        }
    }

    @FXML
    private void handleConfirm() {
        try {
            int carga = Integer.parseInt(cargaField.getText());
            int reps = Integer.parseInt(repsField.getText());

            itemResultado = new ItemPlanoTreino();
            itemResultado.setExercicio(exercicioBase);
            itemResultado.setCargaKg(carga);
            itemResultado.setRepeticoes(reps);

            confirmado = true;
            dialogStage.close();
        } catch (NumberFormatException e) {
            cargaField.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    private void handleRemove() {
        remover = true;
        confirmado = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isConfirmado() { return confirmado; }
    public boolean isRemover() { return remover; }
    public ItemPlanoTreino getItemResultado() { return itemResultado; }
}