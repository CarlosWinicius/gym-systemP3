package br.upe.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class PerfilController extends BaseController {

    @FXML private TextField txtNome;
    @FXML private TextField txtAltura;
    @FXML private TextField txtPeso;
    @FXML private TextField txtGordura;
    @FXML private TextField txtMassaMagra;
    @FXML private TextField txtIMC;
    @FXML private Label lblIMCStatus;

    @FXML
    protected void initialize() {
        // Exemplo de preenchimento inicial
        txtNome.setText("Fulaninho de Tal da Silva");
        txtAltura.setText("170");
        txtPeso.setText("55.6");
        txtGordura.setText("15.2");
        txtMassaMagra.setText("70.8");
        calcularIMC();
    }

    @FXML
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
