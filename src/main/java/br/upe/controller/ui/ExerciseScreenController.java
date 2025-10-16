package br.upe.controller.ui;

import br.upe.controller.business.ExercicioService;
import br.upe.data.beans.Exercicio;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.util.List;

import java.util.logging.Logger;

public class ExerciseScreenController extends BaseController {

    private static final Logger logger = Logger.getLogger(ExerciseScreenController.class.getName());

    @FXML
    private TilePane exerciciosTilePane;
    @FXML
    private Button adicionarButton;
    @FXML
    private SideMenuController sideMenuController;

    private final ExercicioService exercicioService = new ExercicioService();

    @FXML
    public void initialize() {
        carregarExercicios();
    }

    private void carregarExercicios() {
        if (usuarioLogado != null) {
            exerciciosTilePane.getChildren().clear();
            List<Exercicio> exercicios = exercicioService.listarExerciciosDoUsuario(usuarioLogado.getId());
            for (Exercicio exercicio : exercicios) {
                VBox cartao = criarCartaoExercicio(exercicio);
                exerciciosTilePane.getChildren().add(cartao);
            }
        }
    }

    private VBox criarCartaoExercicio(Exercicio exercicio) {
        VBox cartao = new VBox(10);
        cartao.setAlignment(Pos.CENTER);
        cartao.setPrefSize(150, 180);
        cartao.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/" + exercicio.getCaminhoGif()));
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/material-symbols_exercise.png")));
            logger.warning("Não foi possível carregar o GIF: " + exercicio.getCaminhoGif());
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        Label nomeLabel = new Label(exercicio.getNome());
        nomeLabel.setStyle("-fx-font-weight: bold;");

        cartao.getChildren().addAll(imageView, nomeLabel);


        return cartao;
    }

    @FXML
    private void handleAdicionarExercicio() {
        logger.info("Navegando para a tela de adicionar exercício...");
        showAlert(Alert.AlertType.INFORMATION, "Em Construção", "A tela para adicionar/editar exercícios ainda será implementada.");
    }
}