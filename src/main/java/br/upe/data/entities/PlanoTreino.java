package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plano_treino", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "nome"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nome;

    // --- AQUI ESTÁ A MÁGICA QUE FALTAVA ---
    // Mapeia a lista de itens para que ela venha junto com o plano
    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPlanoTreino> itensTreino = new ArrayList<>();

    // Método utilitário que você tinha no Bean
    public void adicionarItem(ItemPlanoTreino item) {
        item.setPlanoTreino(this); // Vincula o pai ao filho
        this.itensTreino.add(item);
    }

    // O toString bonito que você tinha no Bean
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Plano: ").append(id)
                .append(", Usuário: ").append(usuario.getNome())
                .append(", Nome: '").append(nome).append("'\n");

        if (itensTreino == null || itensTreino.isEmpty()) {
            sb.append("  [Este plano não possui exercícios ainda.]");
        } else {
            sb.append("  Exercícios no Plano:\n");
            for (int i = 0; i < itensTreino.size(); i++) {
                sb.append("    ").append(i + 1).append(". ").append(itensTreino.get(i).toString()).append("\n");
            }
        }
        return sb.toString();

    }
    // Adicione dentro da classe PlanoTreino
    public PlanoTreino(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}