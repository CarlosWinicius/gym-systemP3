package br.upe.controller.ui;
import br.upe.controller.business.CalculadoraIMC;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    protected void initialize() {
        Nome.setText("Fulaninho de Tal da Silva");
        Altura.setText("170");
        Peso.setText("55.6");
        PercentualGordura.setText("15.2");
        PercentualMM.setText("70.8");


        double altura = Double.parseDouble(Altura.getText());
        double peso = Double.parseDouble(Peso.getText());


        double imc = CalculadoraIMC.calcular(peso, altura);


        IMC.setText(String.format("%.2f", imc));


        String classificacao = CalculadoraIMC.classificarImc(imc);
        CategoriaIMC.setText(classificacao);
    }
}

