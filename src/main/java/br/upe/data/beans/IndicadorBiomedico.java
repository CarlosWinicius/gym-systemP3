package br.upe.data.beans;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;

public class IndicadorBiomedico {
    private int id;
    private int idUsuario;
    private LocalDate data;
    private double pesoKg;
    private double alturaCm;
    private double percentualGordura;
    private double percentualMassaMagra;
    private double imc;

    public IndicadorBiomedico(){}
    IndicadorBiomedico(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra, double imc) {
        this.id = 0;
        this.idUsuario = idUsuario;
        this.data = data;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.percentualGordura = percentualGordura;
        this.percentualMassaMagra = percentualMassaMagra;
        this.imc = imc;
    }



    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int id = 0;
        private int idUsuario;
        private LocalDate data;
        private double pesoKg;
        private double alturaCm;
        private double percentualGordura;
        private double percentualMassaMagra;
        private double imc;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder idUsuario(int idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }

        public Builder data(LocalDate data) {
            this.data = data;
            return this;
        }

        public Builder pesoKg(double pesoKg) {
            this.pesoKg = pesoKg;
            return this;
        }

        public Builder alturaCm(double alturaCm) {
            this.alturaCm = alturaCm;
            return this;
        }

        public Builder percentualGordura(double percentualGordura) {
            this.percentualGordura = percentualGordura;
            return this;
        }

        public Builder percentualMassaMagra(double percentualMassaMagra) {
            this.percentualMassaMagra = percentualMassaMagra;
            return this;
        }

        public Builder imc(double imc) {
            this.imc = imc;
            return this;
        }

        public IndicadorBiomedico build() {
            IndicadorBiomedico instance = new IndicadorBiomedico(idUsuario, data, pesoKg, alturaCm, percentualGordura, percentualMassaMagra, imc);
            if (id != 0) {
                instance.setId(id);
            }
            return instance;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        this.pesoKg = pesoKg;
    }

    public double getAlturaCm() {
        return alturaCm;
    }

    public void setAlturaCm(double alturaCm) {
        this.alturaCm = alturaCm;
    }

    public double getPercentualGordura() {
        return percentualGordura;
    }

    public void setPercentualGordura(double percentualGordura) {
        this.percentualGordura = percentualGordura;
    }

    public double getPercentualMassaMagra() {
        return percentualMassaMagra;
    }

    public void setPercentualMassaMagra(double percentualMassaMagra) {
        this.percentualMassaMagra = percentualMassaMagra;
    }

    public double getImc() {
        return imc;
    }

    public void setImc(double imc) {
        this.imc = imc;
    }


    @Override
    public String toString() {
        // Configurações para o formato brasileiro, para evitar erros com vírgulas e pontos
        Locale ptBr = Locale.forLanguageTag("pt-BR");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(ptBr);
        symbols.setDecimalSeparator(',');
        DecimalFormat inteiro = new DecimalFormat("#0", symbols);
        DecimalFormat umDecimal = new DecimalFormat("#0.0", symbols);
        DecimalFormat doisDecimais = new DecimalFormat("#0.00", symbols);
        String pesoFormatado = umDecimal.format(pesoKg) + "kg";
        String alturaCmFormatada = inteiro.format(alturaCm) + "cm";
        String gorduraFormatada = umDecimal.format(percentualGordura) + "%";
        String massaMagraFormatada = umDecimal.format(percentualMassaMagra) + "%";
        String imcFormatado = doisDecimais.format(imc);
        return String.format("ID: %d | Data: %-10s | Peso: %s | Altura: %s | Gordura: %s | Massa Magra: %s | IMC: %s",
                id, data, pesoFormatado, alturaCmFormatada, gorduraFormatada, massaMagraFormatada, imcFormatado);
    }
}