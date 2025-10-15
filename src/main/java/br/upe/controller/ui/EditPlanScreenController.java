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

    /**
     * Inicializa a tela com os dados de um plano existente ou de um novo plano.
     */
    public void initData(PlanoTreino plano) {
        this.planoAtual = plano;
        this.planNameField.setText(plano.getNome());
        // Se for um plano novo (sem ID), o botão de deletar é desabilitado.
        deleteButton.setDisable(plano.getIdPlano() == 0);
        populateExerciseGrid();
    }

    /**
     * Carrega e exibe todos os exercícios disponíveis, pré-selecionando os que pertencem ao plano atual.
     */
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
                        // TODO: Abrir modal de edição de carga/reps
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

    /**
     * Salva as alterações do plano (novo nome e lista de exercícios).
     * Distingue entre criar um novo plano e atualizar um existente.
     */
    // Dentro da classe EditPlanScreenController

// ... dentro da classe EditPlanScreenController ...

// Dentro da classe EditPlanScreenController.java

    @FXML
    void handleSavePlan(ActionEvent event) {
        // Guarda o nome antigo para o caso de ser uma edição
        String nomeAntigoDoPlano = planoAtual.getNome();
        String novoNomeDoPlano = planNameField.getText();

        try {
            // --- ETAPA 1: GARANTIR QUE O PLANO EXISTA COM O NOME CORRETO ---

            if (planoAtual.getIdPlano() == 0) {
                // É um plano NOVO. Precisamos criá-lo primeiro.
                logger.info("Criando novo plano com nome: " + novoNomeDoPlano);
                // O serviço retorna o plano com o ID gerado pelo banco. ATUALIZAMOS nossa referência.
                // Isso é CRUCIAL para a próxima etapa.
                this.planoAtual = planoTreinoService.criarPlano(usuarioLogado.getId(), novoNomeDoPlano);
            } else if (!nomeAntigoDoPlano.equals(novoNomeDoPlano)) {
                // É um plano EXISTENTE e o nome foi alterado.
                logger.info("Editando nome do plano de '" + nomeAntigoDoPlano + "' para '" + novoNomeDoPlano + "'");
                planoTreinoService.editarPlano(usuarioLogado.getId(), nomeAntigoDoPlano, novoNomeDoPlano);
                planoAtual.setNome(novoNomeDoPlano); // Atualiza o nome no nosso objeto local também
            }

            // --- ETAPA 2: SINCRONIZAR A LISTA DE EXERCÍCIOS ---

            // Lista de IDs dos exercícios que ESTAVAM no plano ANTES da edição
            Set<Integer> idsOriginais = planoAtual.getItensTreino().stream()
                    .map(ItemPlanoTreino::getIdExercicio)
                    .collect(Collectors.toSet());

            // Lista de IDs dos exercícios que estão SELECIONADOS na tela AGORA
            Set<Integer> idsNovos = cardControllers.stream()
                    .filter(ExerciseCardController::isSelected)
                    .map(controller -> controller.getExercicio().getIdExercicio())
                    .collect(Collectors.toSet());

            // 2a: Remover os que foram desmarcados
            for (Integer idOriginal : idsOriginais) {
                if (!idsNovos.contains(idOriginal)) {
                    logger.info("Removendo exercício ID " + idOriginal + " do plano '" + planoAtual.getNome() + "'");
                    planoTreinoService.removerExercicioDoPlano(usuarioLogado.getId(), planoAtual.getNome(), idOriginal);
                }
            }

            // 2b: Adicionar os que foram marcados
            for (Integer idNovo : idsNovos) {
                if (!idsOriginais.contains(idNovo)) {
                    logger.info("Adicionando exercício ID " + idNovo + " ao plano '" + planoAtual.getNome() + "'");
                    // Adicionamos com carga e repetições padrão. O pop-up futuro cuidaria disso.
                    planoTreinoService.adicionarExercicioAoPlano(usuarioLogado.getId(), planoAtual.getNome(), idNovo, 0, 0);
                }
            }

            logger.info("Plano '" + planoAtual.getNome() + "' salvo com sucesso.");
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano salvo com sucesso!");
            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");

        } catch (Exception e) {
            // O service já lança exceções claras (ex: "Nome já existe"), então podemos mostrá-las ao usuário.
            logger.log(java.util.logging.Level.SEVERE, "Erro ao salvar o plano", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
        }
    }

    /**
     * Deleta o plano de treino atual do sistema.
     */
    @FXML
    void handleDeletePlan(ActionEvent event) {
        try {
            // LÓGICA DE DELEÇÃO REAL (Substituindo o TODO)
            planoTreinoService.deletarPlano(usuarioLogado.getId(), planoAtual.getNome());

            logger.info("Deletando plano: " + planoAtual.getNome());
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Plano deletado com sucesso!");

            // Navega de volta para a tela de listagem
            navigateTo((Node) event.getSource(), "/ui/PlansScreen.fxml");
        } catch (Exception e) {
            logger.severe("Erro ao deletar o plano: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível deletar o plano: " + e.getMessage());
        }
    }
}