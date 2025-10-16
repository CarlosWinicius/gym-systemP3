package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class PerfilController extends BaseController {

    @FXML private TextField Nome;
    @FXML private TextField Altura;
    @FXML private TextField Peso;
    @FXML private TextField PercentualGordura;
    @FXML private TextField PercentualMM;
    @FXML private TextField IMC;
    @FXML private Label CategoriaIMC;


    @FXML
    protected void initialize() {
        Nome.setText("Fulaninho de Tal da Silva");
        Altura.setText("170");
        Peso.setText("55.6");
        PercentualGordura.setText("15.2");
        PercentualMM.setText("70.8");
        IMC.setText("6");
    } }

   /* @FXML
    protected void onSalvar(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Perfil", "Informações salvas com sucesso!");
    }

    @FXML
    protected void calcularIMC() {
        try {
            double peso = Double.parseDouble(txtPeso.getText());
            double altura = Double.parseDouble(txtAltura.getText()) / 100.0; // cm → m

            double imc = peso / (altura * altura);
            txtIMC.setText(String.format("%.2f", imc));

            if (imc < 18.5) {
                lblIMCStatus.setText("abaixo do normal");
            } else if (imc < 25) {
                lblIMCStatus.setText("normal");
            } else if (imc < 30) {
                lblIMCStatus.setText("sobrepeso");
            } else {
                lblIMCStatus.setText("obesidade");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Altura ou peso inválido.");
        }
    }
}
*\
    */
