package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_sessao_treino")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class ItemSessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_sessao")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_sessao", nullable = false)
    private SessaoTreino sessaoTreino;

    @ManyToOne
    @JoinColumn(name = "id_exercicio", nullable = false)
    private Exercicio exercicio;

    @Column(name = "repeticoes_realizadas", nullable = false)
    private Integer repeticoesRealizadas;

    @Column(name = "carga_realizada", nullable = false)
    private Double cargaRealizada;

    @Override
    public String toString() {
        String nomeExerc = (exercicio != null) ? exercicio.getNome() : "Desconhecido";
        return "Exercício: " + nomeExerc + ", Repetições: " + repeticoesRealizadas + ", Carga: " + cargaRealizada + "kg";
    }
}