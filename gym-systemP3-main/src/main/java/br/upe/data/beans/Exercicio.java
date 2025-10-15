package br.upe.data.beans;

public class Exercicio {
    private int idExercicio;
    private int idUsuario;
    private String nome;
    private String descricao;
    private String caminhoGif;

    public Exercicio(int idExercicio, int idUsuario, String nome, String descricao, String caminhoGif) {
        this.idExercicio = idExercicio;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }

    public Exercicio(int idUsuario, String nome, String descricao, String caminhoGif) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }

    public int getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(int idExercicio) {
        this.idExercicio = idExercicio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoGif() {
        return caminhoGif;
    }

    public void setCaminhoGif(String caminhoGif) {
        this.caminhoGif = caminhoGif;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Descrição: %s | GIF: %s", idExercicio, nome, descricao, caminhoGif);
    }
}