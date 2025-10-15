package br.upe.data.beans;

public class ItemPlanoTreino {
    private int idExercicio;
    private int cargaKg;
    private int repeticoes;

    public ItemPlanoTreino(int idExercicio, int cargaKg, int repeticoes) {
        this.idExercicio = idExercicio;
        this.cargaKg = cargaKg;
        this.repeticoes = repeticoes;
    }

    public int getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(int idExercicio) {
        this.idExercicio = idExercicio;
    }

    public int getCargaKg() {
        return cargaKg;
    }

    public void setCargaKg(int cargaKg) {
        this.cargaKg = cargaKg;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    @Override
    public String toString() {
        return "ID Exercício: " + idExercicio + ", Carga: " + cargaKg + "kg, Repetições: " + repeticoes;
    }
}