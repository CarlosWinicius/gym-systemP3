package br.upe.data.beans;

import java.time.LocalDate;

public class IndicadorBiomedico {
    private int id;
    private int idUsuario;
    private LocalDate data;
    private double pesoKg;
    private double alturaCm;
    private double percentualGordura;
    private double percentualMassaMagra;
    private double imc;

    private IndicadorBiomedico(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra, double imc) {
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
        return String.format("ID: %d | Data: %-12s | Peso: %.1fkg | Altura: %.0fcm | Gordura: %.1f%% | Massa Magra: %.1f%% | IMC: %-8.2f",
                id, data, pesoKg, alturaCm, percentualGordura, percentualMassaMagra, imc);
    }
}