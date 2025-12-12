package br.upe.data.beans;

import br.upe.data.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario") // Nome exato da tabela no SQL
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco gera o ID (auto-increment)
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

    @Lob
    @Column(name="foto_perfil")
    private byte[] fotoPerfil;

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }


    public Usuario() {}

    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nome + ", Email: " + email;
    }
}