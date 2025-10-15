package br.upe.data.beans;

import java.util.ArrayList;
import java.util.List;

public class PlanoTreino {
    private int idPlano;
    private int idUsuario;
    private String nome;
    private List<ItemPlanoTreino> itensTreino;

    public PlanoTreino(int idPlano, int idUsuario, String nome, List<ItemPlanoTreino> itensTreino) {
        this.idPlano = idPlano;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.itensTreino = itensTreino;
    }

    public PlanoTreino(int idUsuario, String nome) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.itensTreino = new ArrayList<>();
    }

    public int getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(int idPlano) {
        this.idPlano = idPlano;
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

    public List<ItemPlanoTreino> getItensTreino() {
        return itensTreino;
    }

    public void setItensTreino(List<ItemPlanoTreino> itensTreino) {
        this.itensTreino = itensTreino;
    }

    public void adicionarItem(ItemPlanoTreino item) {
        this.itensTreino.add(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Plano: ").append(idPlano)
                .append(", ID Usuário: ").append(idUsuario)
                .append(", Nome: '").append(nome).append("'\n");
        if (itensTreino.isEmpty()) {
            sb.append("  [Este plano não possui exercícios ainda.]");
        } else {
            sb.append("  Exercícios no Plano:\n");
            for (int i = 0; i < itensTreino.size(); i++) {
                sb.append("    ").append(i + 1).append(". ").append(itensTreino.get(i).toString()).append("\n");
            }
        }
        return sb.toString();
    }
}