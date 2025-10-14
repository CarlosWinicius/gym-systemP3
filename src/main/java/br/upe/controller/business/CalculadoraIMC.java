package br.upe.controller.business;

public class CalculadoraIMC {

    private CalculadoraIMC() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Metodo para verificar as condições
    // Adicionado um comentário para forçar a recompilação
    public static double calcular(double pesoKg, double alturaCm) {
        if (alturaCm <= 0) {
            throw new IllegalArgumentException("Altura deve ser maior que zero para calcular o IMC.");
        }
        // Calcula o IMC
        double alturaMetros = alturaCm / 100.0;
        return pesoKg / (alturaMetros * alturaMetros);
    }

    // Classificação de cada IMC
    public static String classificarImc(double imc) {
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 24.9) {
            return "Peso normal";
        } else if (imc < 29.9) {
            return "Sobrepeso";
        } else if (imc < 34.9) {
            return "Obesidade Grau I";
        } else if (imc < 39.9) {
            return "Obesidade Grau II";
        } else {
            return "Obesidade Grau III";
        }
    }
}