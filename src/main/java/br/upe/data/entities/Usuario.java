package br.upe.data.entities; // Mantive singular para compatibilidade

import br.upe.data.TipoUsuario;
import jakarta.persistence.*;
import lombok.*; // Import do Lombok

@Entity
@Table(name = "usuario")
// Anotações do Lombok para remover o boilerplate:
@Getter                 // Gera todos os Getters
@Setter                 // Gera todos os Setters
@ToString               // Gera o toString()
@NoArgsConstructor      // Gera o construtor vazio obrigatório do JPA (public Usuario() {})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private TipoUsuario tipo;

    // Mantivemos este construtor manualmente porque ele é específico do seu negócio
    // (cria usuário sem pedir ID, pois o ID é auto-gerado).
    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }
}