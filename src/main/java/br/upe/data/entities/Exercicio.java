package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicio")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercicio")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "caminho_gif", nullable = false)
    private String caminhoGif;

    // Dentro da classe Exercicio
    public Exercicio(Integer usuarioId, String nome, String descricao, String caminhoGif) {
        this.id = usuarioId; // ou this.usuario = ... dependendo de como você mapeou
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoGif = caminhoGif;
    }
}