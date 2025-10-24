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
    private TextField Nome;
    @FXML
    private TextField Altura;
    @FXML
    private TextField Peso;
    @FXML
    private TextField PercentualGordura;
    @FXML
    private TextField PercentualMM;
    @FXML
    private TextField IMC;
    @FXML
    private Label CategoriaIMC;
    @FXML
    private Button Salvar;
    @FXML
    private Button EscolherImagem;

    @FXML
    private ImageView Foto;

    private final IUsuarioService usuarioService = new UsuarioService();
    private final IndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();

    private boolean editavel = false;

    private void setCamposEditaveis(boolean editavel) {
        Nome.setEditable(editavel);
        Altura.setEditable(editavel);
        Peso.setEditable(editavel);
        PercentualGordura.setEditable(editavel);
        PercentualMM.setEditable(editavel);
        IMC.setEditable(false);
    }

    @FXML
    protected void initialize() {

        Nome.setText(usuarioLogado.getNome());

        try {
            var lista = indicadorService.listarTodosDoUsuario(usuarioLogado.getId());

            if (!lista.isEmpty()) {
                var ultimo = lista.get(lista.size() - 1);
                Altura.setText(String.valueOf(ultimo.getAlturaCm()));
                Peso.setText(String.valueOf(ultimo.getPesoKg()));
                PercentualGordura.setText(String.valueOf(ultimo.getPercentualGordura()));
                PercentualMM.setText(String.valueOf(ultimo.getPercentualMassaMagra()));
                IMC.setText(String.format("%.2f", ultimo.getImc()));
                CategoriaIMC.setText(CalculadoraIMC.classificarImc(ultimo.getImc()));
            } else {
                Altura.setText("");
                Peso.setText("");
                PercentualGordura.setText("");
                PercentualMM.setText("");
                IMC.setText("");
                CategoriaIMC.setText("");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar dados");
            alert.setHeaderText("Não foi possível carregar os dados biomédicos do usuário.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        PreencherPercentual();
        atualizarIMC();

        setCamposEditaveis(editavel);
    }

    private void atualizarIMC() {
        try {
            if (!Altura.getText().isEmpty() && !Peso.getText().isEmpty()) {
                double altura = Double.parseDouble(Altura.getText());
                double peso = Double.parseDouble(Peso.getText());

                double imc = CalculadoraIMC.calcular(peso, altura);
                IMC.setText(String.format("%.2f", imc));

                String classificacao = CalculadoraIMC.classificarImc(imc);
                CategoriaIMC.setText(classificacao);
            }
        } catch (NumberFormatException e) {
            IMC.setText("Erro");
            CategoriaIMC.setText("Erro");
        }
    }

    private void PreencherPercentual() {
        try {
            if (!PercentualMM.getText().isEmpty() && !PercentualGordura.getText().isEmpty()) {
                double percentualGordura = Double.parseDouble(PercentualMM.getText());
                double percentualMM = Double.parseDouble(PercentualGordura.getText());
            }
        } catch (NumberFormatException e) {
            PercentualGordura.setText("Erro");
            PercentualMM.setText("Erro");
        }
    }

    @FXML
    protected void salvarDados() {
        try {
            String novoNome = Nome.getText().trim();

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

            double altura = Double.parseDouble(Altura.getText());
            double peso = Double.parseDouble(Peso.getText());
            double percentualGordura = Double.parseDouble(PercentualGordura.getText());
            double percentualMM = Double.parseDouble(PercentualMM.getText());

            double imc = CalculadoraIMC.calcular(peso, altura);

            IMC.setText(String.format("%.2f", imc));
            CategoriaIMC.setText(CalculadoraIMC.classificarImc(imc));

            indicadorService.cadastrarIndicador(
                    usuarioLogado.getId(),
                    java.time.LocalDate.now(),
                    peso,
                    altura,
                    percentualGordura,
                    percentualMM
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
    private void EditarTela(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/PerfilEditScreen.fxml"));
            Parent root = loader.load();

            PerfilController controller = loader.getController();
            controller.usuarioLogado = this.usuarioLogado;
            controller.editavel = true;
            controller.setCamposEditaveis(true);

            controller.Nome.setText(usuarioLogado.getNome());
            controller.Altura.setText(Altura.getText());
            controller.Peso.setText(Peso.getText());
            controller.PercentualGordura.setText(PercentualGordura.getText());
            controller.PercentualMM.setText(PercentualMM.getText());
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
            Foto.setImage(image);
        }
    }
}
