package br.upe.controller.ui;

import br.upe.controller.business.CalculadoraIMC;
import br.upe.controller.business.IndicadorBiomedicoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

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
    private Button botaoSalvar;

    private final IndicadorBiomedicoService indicadorService = new IndicadorBiomedicoService();

    // Variáveis para armazenar os dados modificados pelo usuário
    private String nomeSalvo = "";
    private String alturaSalvo = "";
    private String pesoSalvo = "";
    private String percentualGorduraSalvo = "";
    private String percentualMMSalvo = "";

    @FXML
    protected void initialize() {

        Nome.setText("");
        Altura.setText("");
        Peso.setText("");
        PercentualGordura.setText("");
        PercentualMM.setText("");


        restaurarDadosDoArquivo();

        PreencherPercentual();
        atualizarIMC();
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

        nomeSalvo = Nome.getText();
        alturaSalvo = Altura.getText();
        pesoSalvo = Peso.getText();
        percentualGorduraSalvo = PercentualGordura.getText();
        percentualMMSalvo = PercentualMM.getText();


        File arquivo = new File("dados_usuario.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, false))) {
            writer.write("Nome: " + nomeSalvo + "\n");
            writer.write("Altura: " + alturaSalvo + "\n");
            writer.write("Peso: " + pesoSalvo + "\n");
            writer.write("Percentual de Gordura: " + percentualGorduraSalvo + "\n");
            writer.write("Percentual de MM: " + percentualMMSalvo + "\n");
            writer.write("----------------------------------------\n");


            atualizarIMC();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Dados salvos com sucesso!");
            alert.setContentText("Os dados foram salvos corretamente.");
            alert.showAndWait();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao salvar os dados!");
            alert.setContentText("Houve um erro ao tentar salvar os dados.");
            alert.showAndWait();
        }
    }


    private void restaurarDadosDoArquivo() {
        File arquivo = new File("dados_usuario.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.startsWith("Nome: ")) {
                    Nome.setText(linha.substring(6));
                } else if (linha.startsWith("Altura: ")) {
                    Altura.setText(linha.substring(8));
                } else if (linha.startsWith("Peso: ")) {
                    Peso.setText(linha.substring(6));
                } else if (linha.startsWith("Percentual de Gordura: ")) {
                    PercentualGordura.setText(linha.substring(23));
                } else if (linha.startsWith("Percentual de MM: ")) {
                    PercentualMM.setText(linha.substring(18));
                }
            }


            atualizarIMC();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar os dados!");
            alert.setContentText("Houve um erro ao tentar carregar os dados.");
            alert.showAndWait();
        }
    }
}
