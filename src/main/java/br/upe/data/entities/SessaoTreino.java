package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessao_treino")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sessao")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_plano_treino", nullable = false)
    private PlanoTreino planoTreino;

    @Column(name = "data_sessao", nullable = false)
    private LocalDate dataSessao;

    // --- A LISTA QUE FALTAVA ---
    @OneToMany(mappedBy = "sessaoTreino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemSessaoTreino> itensExecutados = new ArrayList<>();

    public void adicionarItemExecutado(ItemSessaoTreino item) {
        item.setSessaoTreino(this); // Importante para salvar no banco
        this.itensExecutados.add(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Sessão: ").append(id)
                .append(", Usuário: ").append(usuario.getNome())
                .append(", Plano Base: ").append(planoTreino.getNome())
                .append(", Data: ").append(dataSessao).append("\n");

        if (itensExecutados == null || itensExecutados.isEmpty()) {
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