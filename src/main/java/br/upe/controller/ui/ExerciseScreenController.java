package br.upe.controller.ui;

import br.upe.controller.business.ExercicioService;
import br.upe.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ExerciseScreenController extends BaseController {

    private static final Logger logger = Logger.getLogger(ExerciseScreenController.class.getName());

    @FXML private TilePane exerciciosTilePane;
    @FXML private Button adicionarButton;
    @FXML private SideMenuController sideMenuController;

    private final ExercicioService exercicioService = new ExercicioService();

    @FXML
    public void initialize() {
        carregarExercicios();
    }

    private void carregarExercicios() {
        if (usuarioLogado == null) return;

        exerciciosTilePane.getChildren().clear();

        List<Exercicio> exerciciosSistema = exercicioService.listarExerciciosDoUsuario(0);
        List<Exercicio> exerciciosUsuario = exercicioService.listarExerciciosDoUsuario(usuarioLogado.getId());

        List<Exercicio> todosExercicios = new ArrayList<>(exerciciosSistema);
        todosExercicios.addAll(exerciciosUsuario);

        for (Exercicio exercicio : todosExercicios) {
            VBox cartao = criarCartaoExercicio(exercicio);
            exerciciosTilePane.getChildren().add(cartao);
        }
    }

    private VBox criarCartaoExercicio(Exercicio exercicio) {
        VBox cartao = new VBox(5);
        cartao.setAlignment(Pos.CENTER);
        cartao.setPrefSize(150, 180);
        cartao.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10; -fx-cursor: hand;");
        cartao.setOnMouseClicked(event -> handleVisualizarExercicio(exercicio));

        ImageView imageView = new ImageView();

        String caminhoGif = exercicio.getCaminhoGif();
        Image image;
        if (caminhoGif != null && !caminhoGif.isEmpty()) {
            try (InputStream stream = getClass().getResourceAsStream("/" + caminhoGif)) {
                if (stream != null) {
                    image = new Image(stream);
                } else {

                    logger.warning("Arquivo de imagem não encontrado: " + caminhoGif);
                    image = new Image(getClass().getResourceAsStream("/images/no image.png"));
                }
            } catch (Exception e) {
                logger.severe("Erro ao carregar imagem: " + caminhoGif + " - " + e.getMessage());
                image = new Image(getClass().getResourceAsStream("/images/no image.png"));
            }
        } else {
            image = new Image(getClass().getResourceAsStream("/images/no image.png"));
        }

        imageView.setImage(image);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        Label nomeLabel = new Label(exercicio.getNome());
        nomeLabel.setStyle("-fx-font-weight: bold;");

        cartao.getChildren().addAll(imageView, nomeLabel);

        if (usuarioLogado != null && exercicio.getIdUsuario() == usuarioLogado.getId()) {
            HBox botoesBox = new HBox(5);
            botoesBox.setAlignment(Pos.CENTER);
            botoesBox.setPadding(new Insets(5, 0, 0, 0));

            Button editarBtn = new Button("Editar");
            editarBtn.setOnAction(e -> {
                e.consume();
                handleEditarExercicio(exercicio);
            });

            Button excluirBtn = new Button("Excluir");
            excluirBtn.setOnAction(e -> {
                e.consume();
                handleExcluirExercicio(exercicio);
            });

            botoesBox.getChildren().addAll(editarBtn, excluirBtn);
            cartao.getChildren().add(botoesBox);
        }

        return cartao;
    }

    @FXML
    private void handleAdicionarExercicio() {
        abrirDialogo(null, DialogMode.NOVO);
    }

    private void handleEditarExercicio(Exercicio exercicio) {
        abrirDialogo(exercicio, DialogMode.EDITAR);
    }

    private void handleVisualizarExercicio(Exercicio exercicio) {
        abrirDialogo(exercicio, DialogMode.VISUALIZAR);
    }

    private void handleExcluirExercicio(Exercicio exercicio) {
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
            logger.severe("Falha ao abrir o diálogo de exercício: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro de Interface", "Não foi possível abrir a tela de detalhes.");
        }
    }
}