package br.upe.data.beans;

public class ItemSessaoTreino {
    private int idExercicio;
    private int repeticoesRealizadas;
    private double cargaRealizada;

    public ItemSessaoTreino(int idExercicio, int repeticoesRealizadas, double cargaRealizada) {
        this.idExercicio = idExercicio;
        this.repeticoesRealizadas = repeticoesRealizadas;
        this.cargaRealizada = cargaRealizada;
    }

    public int getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(int idExercicio) {
        this.idExercicio = idExercicio;
    }

    public int getRepeticoesRealizadas() {
        return repeticoesRealizadas;
    }

    public void setRepeticoesRealizadas(int repeticoesRealizadas) {
        this.repeticoesRealizadas = repeticoesRealizadas;
    }

    public double getCargaRealizada() {
        return cargaRealizada;
    }

    public void setCargaRealizada(double cargaRealizada) {
        this.cargaRealizada = cargaRealizada;
    }

    @Override
    public String toString() {
        return "ID Exercício: " + idExercicio + ", Repetições: " + repeticoesRealizadas + ", Carga: " + cargaRealizada + "kg";
    }
}