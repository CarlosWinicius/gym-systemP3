package br.upe.data.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessaoTreino {
    private int idSessao;
    private int idUsuario;
    private int idPlanoTreino;
    private LocalDate dataSessao;
    private List<ItemSessaoTreino> itensExecutados;

    public SessaoTreino(int idSessao, int idUsuario, int idPlanoTreino, LocalDate dataSessao, List<ItemSessaoTreino> itensExecutados) {
        this.idSessao = idSessao;
        this.idUsuario = idUsuario;
        this.idPlanoTreino = idPlanoTreino;
        this.dataSessao = dataSessao;
        this.itensExecutados = itensExecutados;
    }

    public SessaoTreino(int idUsuario, int idPlanoTreino) {
        this.idUsuario = idUsuario;
        this.idPlanoTreino = idPlanoTreino;
        this.dataSessao = LocalDate.now();
        this.itensExecutados = new ArrayList<>();
    }

    public int getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(int idSessao) {
        this.idSessao = idSessao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPlanoTreino() {
        return idPlanoTreino;
    }

    public void setIdPlanoTreino(int idPlanoTreino) {
        this.idPlanoTreino = idPlanoTreino;
    }

    public LocalDate getDataSessao() {
        return dataSessao;
    }

    public void setDataSessao(LocalDate dataSessao) {
        this.dataSessao = dataSessao;
    }

    public List<ItemSessaoTreino> getItensExecutados() {
        return itensExecutados;
    }

    public void setItensExecutados(List<ItemSessaoTreino> itensExecutados) {
        this.itensExecutados = itensExecutados;
    }

    public void adicionarItemExecutado(ItemSessaoTreino item) {
        this.itensExecutados.add(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Sessão: ").append(idSessao)
                .append(", ID Usuário: ").append(idUsuario)
                .append(", ID Plano: ").append(idPlanoTreino)
                .append(", Data: ").append(dataSessao).append("\n");
        if (itensExecutados.isEmpty()) {
            sb.append("  [Nenhum exercício registrado nesta sessão.]");
        } else {
            sb.append("  Exercícios Registrados:\n");
            for (int i = 0; i < itensExecutados.size(); i++) {
                sb.append("    ").append(i + 1).append(". ").append(itensExecutados.get(i).toString()).append("\n");
            }
        }
        return sb.toString();
    }
}