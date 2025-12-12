package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_plano_treino")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class ItemPlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_plano")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_plano", nullable = false)
    private PlanoTreino planoTreino;

    @ManyToOne
    @JoinColumn(name = "id_exercicio", nullable = false)
    private Exercicio exercicio;

    @Column(name = "carga_kg", nullable = false)
    private Integer cargaKg;

    @Column(nullable = false)
    private Integer repeticoes;

    @Override
    public String toString() {
        // Apenas dados simples, sem chamar o objeto completo 'planoTreino'
        String nomeExerc = (exercicio != null) ? exercicio.getNome() : "Desconhecido";
        return "Exercício: " + nomeExerc + ", Carga: " + cargaKg + "kg, Repetições: " + repeticoes;
    }
}