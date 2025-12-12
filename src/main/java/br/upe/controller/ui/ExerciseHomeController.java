package br.upe.controller.ui;

import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExerciseHomeController {

    // Logger para substituir o printStackTrace (Sonar adora isso)
    private static final Logger LOGGER = Logger.getLogger(ExerciseHomeController.class.getName());
    private static final String STYLE_BUTTON_PADRAO = "button-padrao";
    private static final String DEFAULT_VALUE_ZERO = "0";

    @FXML private ImageView exerciseImage;
    @FXML private Label exerciseName;
    @FXML private Label exerciseName1;
    @FXML private TextField cargaField;
    @FXML private TextField repsField;

    @FXML private Button btnSalvar;
    @FXML private Button btnRemover;

    private Stage dialogStage;

    // Dependência injetada ou instanciada (usando Interface para desacoplamento)
    private final IPlanoTreinoService planoService = new PlanoTreinoService();

    private List<ItemPlanoTreino> listaExercicios;
    private int indiceAtual = 0;
    private int cargaOriginal;
    private int repsOriginal;

    @FXML
    public void initialize() {
        repsField.setEditable(false);

        // Validação de entrada numérica
        cargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cargaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Aplica o estilo CSS via código para garantir (em vez de setStyle manual)
        btnSalvar.getStyleClass().add(STYLE_BUTTON_PADRAO);
        btnRemover.getStyleClass().add(STYLE_BUTTON_PADRAO);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setItensDoPlano(List<ItemPlanoTreino> itens) {
        this.listaExercicios = itens;
        this.indiceAtual = 0;

        if (listaExercicios != null && !listaExercicios.isEmpty()) {
            carregarExercicioAtual();
        }
    }

    private void carregarExercicioAtual() {
        ItemPlanoTreino itemAtual = listaExercicios.get(indiceAtual);
        Exercicio exercicio = itemAtual.getExercicio();

        this.cargaOriginal = itemAtual.getCargaKg();
        this.repsOriginal = itemAtual.getRepeticoes();

        exerciseName.setText(exercicio.getNome());
        cargaField.setText(String.valueOf(this.cargaOriginal));
        repsField.setText(String.valueOf(this.repsOriginal));

        // Atualiza o contador (Ex: Exercício 1 / 5)
        exerciseName1.setText(String.format("Exercício %d / %d", (indiceAtual + 1), listaExercicios.size()));

        carregarImagem(exercicio);
        configurarBotoes();
    }

    private void configurarBotoes() {
        btnRemover.setVisible(indiceAtual > 0);

        // Define apenas o texto. A cor vem da classe CSS 'button-padrao'
        if (indiceAtual == listaExercicios.size() - 1) {
            btnSalvar.setText("Finalizar");
        } else {
            btnSalvar.setText("Próximo");
        }
    }

    private boolean verificarSalvarAlteracoes() {
        try {
            if (cargaField.getText().isBlank()) cargaField.setText(DEFAULT_VALUE_ZERO);

            int cargaAtual = Integer.parseInt(cargaField.getText());
            int repsAtual = Integer.parseInt(repsField.getText());

            // Se não houve alteração, prossegue sem perguntar
            if (cargaAtual == cargaOriginal && repsAtual == repsOriginal) {
                return true;
            }

            return exibirDialogoConfirmacao(cargaAtual, repsAtual);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Erro de formato numérico ao verificar alterações", e);
            return true; // Prossegue ignorando erro de parse
        }
    }

    private boolean exibirDialogoConfirmacao(int cargaAtual, int repsAtual) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alterações Detectadas");
        alert.setHeaderText("Você alterou a carga ou repetições.");
        alert.setContentText("Deseja salvar essas alterações no seu plano permanentemente?");

        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não");
        alert.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == btnSim) {
            salvarAlteracoesNoBanco(cargaAtual, repsAtual);
            return true;
        }

        return result.isPresent() && result.get() == btnNao;
    }

    private void salvarAlteracoesNoBanco(int cargaAtual, int repsAtual) {
        ItemPlanoTreino item = listaExercicios.get(indiceAtual);
        item.setCargaKg(cargaAtual);
        item.setRepeticoes(repsAtual);

        try {
            // Chamada limpa via Interface (sem casting feio)
            planoService.atualizarItemTreino(item);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar item de treino no banco", e);
            mostrarErro("Não foi possível salvar a alteração.");
        }
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleProximo() {
        if (verificarSalvarAlteracoes()) {
            if (indiceAtual < listaExercicios.size() - 1) {
                indiceAtual++;
                carregarExercicioAtual();
            } else {
                dialogStage.close();
            }
        }
    }

    @FXML
    private void handleAnterior() {
        if (verificarSalvarAlteracoes()) {
            if (indiceAtual > 0) {
                indiceAtual--;
                carregarExercicioAtual();
            }
        }
    }

    private void carregarImagem(Exercicio exercicio) {
        try {
            String caminho = exercicio.getCaminhoGif();
            if (caminho != null && !caminho.isBlank()) {
                // Tenta carregar o GIF específico
                String fullPath = "/gif/" + new File(caminho).getName();
                var url = getClass().getResource(fullPath);

                if (url != null) {
                    exerciseImage.setImage(new Image(url.toString()));
                    return;
                }
            }
            // Se falhar ou não tiver caminho, carrega padrão
            imagemPadrao();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao carregar imagem do exercício: " + exercicio.getNome(), e);
            imagemPadrao();
        }
    }

    private void imagemPadrao() {
        try {
            var resource = getClass().getResourceAsStream("/images/no image.png");
            if (resource != null) {
                exerciseImage.setImage(new Image(resource));
            }
        } catch (Exception ignored) {
            // Ignora silenciosamente se nem a imagem padrão existir
        }
    }

    @FXML
    private void incrementReps() {
        alterarRepeticoes(1);
    }

    @FXML
    private void decrementReps() {
        alterarRepeticoes(-1);
    }

    private void alterarRepeticoes(int delta) {
        try {
            int val = Integer.parseInt(repsField.getText());
            int novoValor = Math.max(0, val + delta); // Evita números negativos
            repsField.setText(String.valueOf(novoValor));
        } catch (NumberFormatException e) {
            repsField.setText(delta > 0 ? "1" : "0");
        }
    }
}