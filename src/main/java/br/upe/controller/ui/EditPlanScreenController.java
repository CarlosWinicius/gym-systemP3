package br.upe.controller.ui;

import br.upe.controller.business.ExercicioService;
import br.upe.controller.business.IExercicioService;
import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.entities.Exercicio;
import br.upe.data.entities.ItemPlanoTreino;
import br.upe.data.entities.PlanoTreino;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EditPlanScreenController extends BaseController {

    @FXML private TextField planNameField;
    @FXML private TilePane exercisesTilePane;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    private PlanoTreino planoAtual;
    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();
    private final IExercicioService exercicioService = new ExercicioService();

    private final Map<Integer, ItemPlanoTreino> itensTemporarios = new HashMap<>();
    private final List<ExerciseCardController> cardControllers = new ArrayList<>();

    public void initData(PlanoTreino plano) {
        this.planoAtual = plano;
        this.planNameField.setText(plano.getNome());

        boolean isNovoPlano = (plano.getId() == null || plano.getId() == 0);
        deleteButton.setDisable(isNovoPlano);

        itensTemporarios.clear();
        if (planoAtual.getItensTreino() != null) {
            for (ItemPlanoTreino item : planoAtual.getItensTreino()) {
                itensTemporarios.put(item.getExercicio().getId(), item);
            }
        }

        populateExerciseGrid();
    }

    private void populateExerciseGrid() {
        exercisesTilePane.getChildren().clear();
        cardControllers.clear();

        List<Exercicio> todosExercicios = exercicioService.listarExerciciosDoUsuario(usuarioLogado.getId());

        for (Exercicio exercicio : todosExercicios) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseCard.fxml"));
                VBox cardNode = loader.load();
                ExerciseCardController controller = loader.getController();
                controller.setData(exercicio);

                if (itensTemporarios.containsKey(exercicio.getId())) {
                    controller.setSelected(true);
                }

                cardNode.setOnMouseClicked(event -> handleExerciseClick(controller, exercicio));
                cardControllers.add(controller);
                exercisesTilePane.getChildren().add(cardNode);
            } catch (IOException e) {
                logger.severe("Falha ao carregar card: " + e.getMessage());
            }
        }
    }

    private void handleExerciseClick(ExerciseCardController card, Exercicio exercicio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseItemDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Configurar Exercício");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(saveButton.getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(page));

            ExerciseItemDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            ItemPlanoTreino itemExistente = itensTemporarios.get(exercicio.getId());
            controller.setDados(exercicio, itemExistente);

            dialogStage.showAndWait();

            if (controller.isRemover()) {
                itensTemporarios.remove(exercicio.getId());
                card.setSelected(false);
            } else if (controller.isConfirmado()) {
                ItemPlanoTreino novoItem = controller.getItemResultado();
                itensTemporarios.put(exercicio.getId(), novoItem);
                card.setSelected(true);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao abrir diálogo do exercício", e);
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a janela de detalhes.");
        }
    }

    @FXML
    void handleSavePlan(ActionEvent event) {
        String nomeAntigo = planoAtual.getNome();
        String novoNome = planNameField.getText();

        if (novoNome.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O nome do plano não pode ser vazio.");
            return;
        }

        saveButton.setDisable(true);
        saveButton.setText("Salvando...");

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (planoAtual.getId() == null || planoAtual.getId() == 0) {
                    planoAtual = planoTreinoService.criarPlano(usuarioLogado.getId(), novoNome);
                } else if (!nomeAntigo.equals(novoNome)) {
                    planoTreinoService.editarPlano(usuarioLogado.getId(), nomeAntigo, novoNome);
                    planoAtual.setNome(novoNome);
                }

                Set<Integer> idsNoBanco = new HashSet<>();
                if (planoAtual.getItensTreino() != null) {
                    idsNoBanco = planoAtual.getItensTreino().stream()
                            .map(item -> item.getExercicio().getId())
                            .collect(Collectors.toSet());
                }

                Set<Integer> idsSelecionados = itensTemporarios.keySet();

                for (Integer idBanco : idsNoBanco) {
                    if (!idsSelecionados.contains(idBanco)) {
                        planoTreinoService.removerExercicioDoPlano(usuarioLogado.getId(), planoAtual.getNome(), idBanco);
                    }
                }

                for (Integer idNovo : idsSelecionados) {
                    ItemPlanoTreino item = itensTemporarios.get(idNovo);

                    if (!idsNoBanco.contains(idNovo)) {
                        planoTreinoService.adicionarExercicioAoPlano(
                                usuarioLogado.getId(),
                                planoAtual.getNome(),
                                item.getExercicio().getId(),
                                item.getRepeticoes(),
                                item.getCargaKg()
                        );
                    } else {
                        planoTreinoService.removerExercicioDoPlano(usuarioLogado.getId(), planoAtual.getNome(), idNovo);
                        planoTreinoService.adicionarExercicioAoPlano(
                                usuarioLogado.getId(),
                                planoAtual.getNome(),
                                item.getExercicio().getId(),
                                item.getRepeticoes(),
                                item.getCargaKg()
                        );
                    }
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            saveButton.setDisable(false);
            saveButton.setText("Salvar Plano");
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano salvo com sucesso!");
            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");
        });

        saveTask.setOnFailed(e -> {
            saveButton.setDisable(false);
            saveButton.setText("Salvar Plano");
            Throwable erro = saveTask.getException();
            logger.log(Level.SEVERE, "Erro ao salvar plano", erro);
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao salvar: " + erro.getMessage());
        });

        new Thread(saveTask).start();
    }

    @FXML
    void handleDeletePlan(ActionEvent event) {
        deleteButton.setDisable(true);
        deleteButton.setText("Excluindo...");
        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                planoTreinoService.deletarPlano(usuarioLogado.getId(), planoAtual.getNome());
                return null;
            }
        };
        deleteTask.setOnSucceeded(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano excluído!");
            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");
        });
        deleteTask.setOnFailed(e -> {
            deleteButton.setDisable(false);
            deleteButton.setText("Excluir");
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao excluir: " + deleteTask.getException().getMessage());
        });
        new Thread(deleteTask).start();
    }
}