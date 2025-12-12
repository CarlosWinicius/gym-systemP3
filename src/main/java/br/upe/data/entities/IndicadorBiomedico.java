package br.upe.data.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "indicador_biomedico")
@Getter @Setter @ToString(exclude = "usuario")
@NoArgsConstructor @AllArgsConstructor
public class IndicadorBiomedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Mantendo o nome da coluna do banco, mas o Java pode ser ajustado se preferir
    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    // ADICIONADO: O campo que faltava
    @Column(name = "altura_cm", nullable = false)
    private Double alturaCm;

    @Column(name = "percentual_gordura")
    private Double percentualGordura;

    @Column(name = "percentual_massa_magra")
    private Double percentualMassaMagra;

    @Column(nullable = false)
    private Double imc;

    public IndicadorBiomedico(Usuario usuario, LocalDate dataRegistro, Double pesoKg, Double alturaCm, Double percentualGordura, Double percentualMassaMagra, Double imc) {
        this.usuario = usuario;
        this.dataRegistro = dataRegistro;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.percentualGordura = percentualGordura;
        this.percentualMassaMagra = percentualMassaMagra;
        this.imc = imc;
    }
}