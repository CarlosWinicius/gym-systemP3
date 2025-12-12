package br.upe.controller.ui;

import br.upe.controller.business.ExercicioService;
import br.upe.data.entities.Exercicio;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ExerciseScreenController extends BaseController {

    @FXML private TilePane exerciciosTilePane;
    @FXML private Button adicionarButton;

    private ProgressIndicator loadingSpinner = new ProgressIndicator();

    private final ExercicioService exercicioService = new ExercicioService();

    @FXML
    public void initialize() {

        loadingSpinner.setVisible(false);
        if (exerciciosTilePane.getParent() instanceof VBox) {
            // Adiciona o spinner ao layout pai
        }
        carregarExercicios();
    }

    private void carregarExercicios() {
        if (usuarioLogado == null) return;

        exerciciosTilePane.setVisible(false);

        Task<List<Exercicio>> task = new Task<>() {
            @Override
            protected List<Exercicio> call() throws Exception {
                List<Exercicio> exerciciosSistema = exercicioService.listarExerciciosDoUsuario(0);
                List<Exercicio> exerciciosUsuario = exercicioService.listarExerciciosDoUsuario(usuarioLogado.getId());

                List<Exercicio> todos = new ArrayList<>(exerciciosSistema);
                todos.addAll(exerciciosUsuario);
                return todos;
            }
        };

        task.setOnSucceeded(event -> {
            List<Exercicio> exercicios = task.getValue();
            exerciciosTilePane.getChildren().clear();

            for (Exercicio exercicio : exercicios) {
                try {
                    criarEAdicionarCard(exercicio);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Erro ao criar card", e);
                }
            }
            exerciciosTilePane.setVisible(true);
        });

        task.setOnFailed(event -> {
            Throwable erro = task.getException();
            logger.log(Level.SEVERE, "Falha ao buscar exercícios", erro);
            showAlert(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível carregar os exercícios do servidor.");
            exerciciosTilePane.setVisible(true);
        });

        new Thread(task).start();
    }

    private void criarEAdicionarCard(Exercicio exercicio) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseCard.fxml"));
        VBox cardNode = loader.load();

        ExerciseCardController cardController = loader.getController();
        cardController.setData(exercicio, this);

        exerciciosTilePane.getChildren().add(cardNode);
    }

    @FXML
    private void handleAdicionarExercicio() {
        abrirDialogo(null, DialogMode.NOVO);
    }

    public void handleEditarExercicio(Exercicio exercicio) {
        abrirDialogo(exercicio, DialogMode.EDITAR);
    }

    public void handleVisualizarExercicio(Exercicio exercicio) {
        abrirDialogo(exercicio, DialogMode.VISUALIZAR);
    }

    public void handleExcluirExercicio(Exercicio exercicio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Excluir: " + exercicio.getNome());
        alert.setContentText("Você tem certeza que deseja excluir este exercício? Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                exercicioService.deletarExercicioPorNome(usuarioLogado.getId(), exercicio.getNome());
                carregarExercicios();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro ao Excluir", "Não foi possível excluir o exercício.");
            }
        }
    }

    private void abrirDialogo(Exercicio exercicio, DialogMode modo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Detalhes do Exercício");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(adicionarButton.getScene().getWindow());

            Scene scene = new Scene(page, 600, 400);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            ExercicioDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.configurarModo(modo, exercicio);

            dialogStage.showAndWait();

            if (controller.isSalvo()) {
                if (modo == DialogMode.NOVO) {
                    exercicioService.cadastrarExercicio(
                            usuarioLogado.getId(),
                            controller.getExercicio().getNome(),
                            controller.getExercicio().getDescricao(),
                            controller.getExercicio().getCaminhoGif()
                    );
                } else if (modo == DialogMode.EDITAR) {
                    exercicioService.atualizarExercicio(
                            usuarioLogado.getId(),
                            exercicio.getNome(),
                            controller.getExercicio().getNome(),
                            controller.getExercicio().getDescricao(),
                            controller.getExercicio().getCaminhoGif()
                    );
                }
                carregarExercicios();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Falha ao abrir o diálogo de exercício ''{0}'': {1}", new Object[]{e.getMessage(), e});
            showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível abrir a tela de detalhes.");
        }
    }
}