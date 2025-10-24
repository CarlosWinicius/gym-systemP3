package br.upe.controller.ui;

import br.upe.controller.business.CalculadoraIMC;
import br.upe.controller.business.IUsuarioService;
import br.upe.controller.business.IndicadorBiomedicoService;
import br.upe.controller.business.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PerfilController extends BaseController {

    @FXML
    private TextField nome;
    @FXML
    private TextField altura;
    @FXML
    private TextField peso;
    @FXML
    private TextField percentualGordura;
    @FXML
    private TextField percentualMM;
    @FXML
    private TextField imc;
    @FXML
    private Label categoriaIMC;
    @FXML
    private Button salvar;
    @FXML
    private Button escolherImagem;
    @FXML
    private ImageView foto;

    private final IUsuarioService usuarioService = new UsuarioService();
    private final IndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();

    private boolean editavel = false;

    private void setCamposEditaveis(boolean editavel) {
        nome.setEditable(editavel);
        altura.setEditable(editavel);
        peso.setEditable(editavel);
        percentualGordura.setEditable(editavel);
        percentualMM.setEditable(editavel);
        imc.setEditable(false);
    }

    @FXML
    protected void initialize() {
        nome.setText(usuarioLogado.getNome());

        try {
            var lista = indicadorService.listarTodosDoUsuario(usuarioLogado.getId());

            if (!lista.isEmpty()) {
                var ultimo = lista.get(lista.size() - 1);
                altura.setText(String.valueOf(ultimo.getAlturaCm()));
                peso.setText(String.valueOf(ultimo.getPesoKg()));
                percentualGordura.setText(String.valueOf(ultimo.getPercentualGordura()));
                percentualMM.setText(String.valueOf(ultimo.getPercentualMassaMagra()));
                imc.setText(String.format("%.2f", ultimo.getImc()));
                categoriaIMC.setText(CalculadoraIMC.classificarImc(ultimo.getImc()));
            } else {
                altura.setText("");
                peso.setText("");
                percentualGordura.setText("");
                percentualMM.setText("");
                imc.setText("");
                categoriaIMC.setText("");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar dados");
            alert.setHeaderText("Não foi possível carregar os dados biomédicos do usuário.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        preencherPercentual();
        atualizarIMC();

        setCamposEditaveis(editavel);
    }

    private void atualizarIMC() {
        try {
            if (!altura.getText().isEmpty() && !peso.getText().isEmpty()) {
                double alturaValor = Double.parseDouble(altura.getText());
                double pesoValor = Double.parseDouble(peso.getText());

                double resultadoIMC = CalculadoraIMC.calcular(pesoValor, alturaValor);
                imc.setText(String.format("%.2f", resultadoIMC));

                String classificacao = CalculadoraIMC.classificarImc(resultadoIMC);
                categoriaIMC.setText(classificacao);
            }
        } catch (NumberFormatException e) {
            imc.setText("Erro");
            categoriaIMC.setText("Erro");
        }
    }

    private void preencherPercentual() {
        try {
            if (!percentualMM.getText().isEmpty() && !percentualGordura.getText().isEmpty()) {
                Double.parseDouble(percentualMM.getText());
                Double.parseDouble(percentualGordura.getText());
            }
        } catch (NumberFormatException e) {
            percentualGordura.setText("Erro");
            percentualMM.setText("Erro");
        }
    }

    @FXML
    protected void salvarDados() {
        try {
            String novoNome = nome.getText().trim();

            if (novoNome.isEmpty()) {
                throw new IllegalArgumentException("O nome não pode estar vazio.");
            }

            usuarioLogado.setNome(novoNome);

            usuarioService.atualizarUsuario(
                    usuarioLogado.getId(),
                    novoNome,
                    usuarioLogado.getEmail(),
                    usuarioLogado.getSenha(),
                    usuarioLogado.getTipo()
            );

            double alturaValor = Double.parseDouble(altura.getText());
            double pesoValor = Double.parseDouble(peso.getText());
            double percentualGorduraValor = Double.parseDouble(percentualGordura.getText());
            double percentualMMValor = Double.parseDouble(percentualMM.getText());

            double resultadoIMC = CalculadoraIMC.calcular(pesoValor, alturaValor);

            imc.setText(String.format("%.2f", resultadoIMC));
            categoriaIMC.setText(CalculadoraIMC.classificarImc(resultadoIMC));

            indicadorService.cadastrarIndicador(
                    usuarioLogado.getId(),
                    java.time.LocalDate.now(),
                    pesoValor,
                    alturaValor,
                    percentualGorduraValor,
                    percentualMMValor
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Dados salvos com sucesso!");
            alert.setContentText("As informações foram atualizadas corretamente.");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de formato");
            alert.setHeaderText("Erro nos dados numéricos");
            alert.setContentText("Verifique se altura, peso e percentuais são números válidos.");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao salvar os dados!");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void voltarTela(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui/PerfilScreen.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarTela(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PerfilEditScreen.fxml"));
            Parent root = loader.load();

            PerfilController controller = loader.getController();
            controller.usuarioLogado = this.usuarioLogado;
            controller.editavel = true;
            controller.setCamposEditaveis(true);

            controller.nome.setText(usuarioLogado.getNome());
            controller.altura.setText(altura.getText());
            controller.peso.setText(peso.getText());
            controller.percentualGordura.setText(percentualGordura.getText());
            controller.percentualMM.setText(percentualMM.getText());
            controller.atualizarIMC();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void escolherImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            foto.setImage(image);
        }
    }
}
