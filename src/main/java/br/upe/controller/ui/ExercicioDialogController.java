package br.upe.controller.ui;

import br.upe.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

enum                                           DialogMode {
    NOVO,
    EDITAR,
    VISUALIZAR
}

public class ExercicioDialogController extends BaseController {

    @FXML private TextArea descricaoTextArea;
    @FXML private Button excluirButton;
    @FXML private ImageView gifImageView;
    @FXML private TextField nomeTextField;
    @FXML private Button salvarButton;
    @FXML private Button selecionarArquivoButton;
    @FXML private Label tituloLabel;

    private Stage dialogStage;
    private Exercicio exercicio;
    private boolean salvo = false;
    private String nomeArquivoGif = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void configurarModo(DialogMode mode, Exercicio exercicio) {
        this.exercicio = exercicio;
        if (exercicio != null) {
            this.nomeArquivoGif = exercicio.getCaminhoGif();
        }

        switch (mode) {
            case NOVO:
                tituloLabel.setText("Novo Exercício");
                salvarButton.setText("Adicionar");
                excluirButton.setVisible(false);
                break;
            case EDITAR:
                tituloLabel.setText("Editar Exercício");
                salvarButton.setText("Salvar");
                excluirButton.setText("Cancelar");
                excluirButton.setStyle("-fx-background-color: #7f8c8d;"); // Cor cinza para cancelar
                preencherDados();
                break;
            case VISUALIZAR:
                tituloLabel.setText("Visualizar Exercício");
                nomeTextField.setEditable(false);
                descricaoTextArea.setEditable(false);
                salvarButton.setVisible(false);
                excluirButton.setText("Fechar");
                selecionarArquivoButton.setVisible(false);
                preencherDados();
                break;
        }
    }

    private void preencherDados() {
        if (exercicio == null) return;

        nomeTextField.setText(exercicio.getNome());
        descricaoTextArea.setText(exercicio.getDescricao());

        if (nomeArquivoGif != null && !nomeArquivoGif.isEmpty()) {
            try {
                String caminhoResource = "/gif/" + nomeArquivoGif;
                Image image = new Image(getClass().getResourceAsStream(caminhoResource));
                gifImageView.setImage(image);
            } catch (Exception e) {
                Image placeholder = new Image(getClass().getResourceAsStream("/images/no image.png"));
                gifImageView.setImage(placeholder);
            }
        } else {
            Image placeholder = new Image(getClass().getResourceAsStream("/images/no image.png"));
            gifImageView.setImage(placeholder);
        }
    }

    @FXML
    private void handleSelecionarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um GIF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.gif"));
        File arquivoSelecionado = fileChooser.showOpenDialog(dialogStage);
        if (arquivoSelecionado != null) {
            try {
                Path destino = Paths.get("src/main/resources/gif/" + arquivoSelecionado.getName());
                Files.copy(arquivoSelecionado.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
                this.nomeArquivoGif = arquivoSelecionado.getName();
                Image image = new Image(arquivoSelecionado.toURI().toString());
                gifImageView.setImage(image);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao Salvar Arquivo", "Não foi possível copiar a imagem.");
            }
        }
    }

    @FXML
    private void handleSalvar() {
        if (isInputValid()) {
            if (exercicio == null) {
                this.exercicio = new Exercicio(usuarioLogado.getId(), nomeTextField.getText(), descricaoTextArea.getText(), nomeArquivoGif);
            } else {
                exercicio.setNome(nomeTextField.getText());
                exercicio.setDescricao(descricaoTextArea.getText());
                exercicio.setCaminhoGif(nomeArquivoGif);
            }
            salvo = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (nomeTextField.getText() == null || nomeTextField.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Nome' é obrigatório.\n";
        }
        if (descricaoTextArea.getText() == null || descricaoTextArea.getText().trim().isEmpty()) {
            errorMessage += "O campo 'Descrição' é obrigatório.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", errorMessage);
            return false;
        }
    }

    public boolean isSalvo() { return salvo; }
    public Exercicio getExercicio() { return exercicio; }
}