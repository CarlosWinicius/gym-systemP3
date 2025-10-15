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

    //O mockito precisou de um construtor padrão para poder realizar os mocks
    public IndicadorBiomedico(){}

    public IndicadorBiomedico(int id, int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra, double imc) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.data = data;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.percentualGordura = percentualGordura;
        this.percentualMassaMagra = percentualMassaMagra;
        this.imc = imc;
    }

    public IndicadorBiomedico(int idUsuario, LocalDate data, double pesoKg, double alturaCm, double percentualGordura, double percentualMassaMagra, double imc) {
        this.idUsuario = idUsuario;
        this.data = data;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.percentualGordura = percentualGordura;
        this.percentualMassaMagra = percentualMassaMagra;
        this.imc = imc;
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