package br.upe.controller.ui;

import br.upe.controller.business.ExercicioService;
import br.upe.controller.business.IExercicioService;
import br.upe.controller.business.IPlanoTreinoService;
import br.upe.controller.business.PlanoTreinoService;
import br.upe.data.beans.Exercicio;
import br.upe.data.beans.ItemPlanoTreino;
import br.upe.data.beans.PlanoTreino;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EditPlanScreenController extends BaseController {

    @FXML
    private TextField planNameField;
    @FXML
    private TilePane exercisesTilePane;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;

    private PlanoTreino planoAtual;
    private final IPlanoTreinoService planoTreinoService = new PlanoTreinoService();
    private final IExercicioService exercicioService = new ExercicioService();
    private final List<ExerciseCardController> cardControllers = new ArrayList<>();

    public void initData(PlanoTreino plano) {
        this.planoAtual = plano;
        this.planNameField.setText(plano.getNome());
        deleteButton.setDisable(plano.getIdPlano() == 0);
        populateExerciseGrid();
    }

    private void populateExerciseGrid() {
        exercisesTilePane.getChildren().clear();
        cardControllers.clear();

        List<Exercicio> todosExercicios = exercicioService.listarExerciciosDoUsuario(usuarioLogado.getId());
        List<Integer> idsExerciciosNoPlano = planoAtual.getItensTreino().stream()
                .map(ItemPlanoTreino::getIdExercicio)
                .collect(Collectors.toList());

        for (Exercicio exercicio : todosExercicios) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ExerciseCard.fxml"));
                VBox cardNode = loader.load();
                ExerciseCardController controller = loader.getController();
                controller.setData(exercicio);

                if (idsExerciciosNoPlano.contains(exercicio.getIdExercicio())) {
                    controller.setSelected(true);
                }

                cardNode.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        logger.info("Clique duplo em: " + controller.getExercicio().getNome());
                    } else {
                        controller.toggleSelection();
                    }
                });

                cardControllers.add(controller);
                exercisesTilePane.getChildren().add(cardNode);
            } catch (IOException e) {
                logger.severe("Falha ao carregar o componente ExerciseCard.fxml: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleSavePlan(ActionEvent event) {
        String nomeAntigoDoPlano = planoAtual.getNome();
        String novoNomeDoPlano = planNameField.getText();

        try {
            if (planoAtual.getIdPlano() == 0) {
                logger.log(Level.INFO, "Criando novo plano com nome: {0}", novoNomeDoPlano);

                this.planoAtual = planoTreinoService.criarPlano(usuarioLogado.getId(), novoNomeDoPlano);
            } else if (!nomeAntigoDoPlano.equals(novoNomeDoPlano)) {
                logger.log(Level.INFO, "Editando nome do plano de ''{0}'' para ''{1}''", new Object[]{nomeAntigoDoPlano, novoNomeDoPlano});
                planoTreinoService.editarPlano(usuarioLogado.getId(), nomeAntigoDoPlano, novoNomeDoPlano);
                planoAtual.setNome(novoNomeDoPlano);
            }

            Set<Integer> idsOriginais = planoAtual.getItensTreino().stream()
                    .map(ItemPlanoTreino::getIdExercicio)
                    .collect(Collectors.toSet());

            Set<Integer> idsNovos = cardControllers.stream()
                    .filter(ExerciseCardController::isSelected)
                    .map(controller -> controller.getExercicio().getIdExercicio())
                    .collect(Collectors.toSet());

            for (Integer idOriginal : idsOriginais) {
                if (!idsNovos.contains(idOriginal)) {
                    logger.log(Level.INFO, "Removendo exercício ID {0} do plano ''{1}''", new Object[]{idOriginal, planoAtual.getNome()});
                    planoTreinoService.removerExercicioDoPlano(usuarioLogado.getId(), planoAtual.getNome(), idOriginal);
                }
            }

            for (Integer idNovo : idsNovos) {
                if (!idsOriginais.contains(idNovo)) {
                    logger.log(Level.INFO, "Adicionando exercício ID {0} ao plano ''{1}''", new Object[]{idNovo, planoAtual.getNome()});

                    planoTreinoService.adicionarExercicioAoPlano(usuarioLogado.getId(), planoAtual.getNome(), idNovo, 0, 0);
                }
            }

            logger.info("Plano '" + planoAtual.getNome() + "' salvo com sucesso.");
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano salvo com sucesso!");
            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Erro ao salvar o plano", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
        }
    }


    @FXML
    void handleDeletePlan(ActionEvent event) {
        try {
            planoTreinoService.deletarPlano(usuarioLogado.getId(), planoAtual.getNome());

            logger.info("Deletando plano: " + planoAtual.getNome());
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano deletado com sucesso!");

            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");
        } catch (Exception e) {
            logger.severe("Erro ao deletar o plano: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível deletar o plano: " + e.getMessage());
        }
    }
}