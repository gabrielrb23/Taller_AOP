package com.ejemplo.Taller_AOP.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "notas")


public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La materia es obligatoria")
    @Column(nullable = false)
    private String materia;

    private String observacion;

    @NotNull @DecimalMin("0.0") @DecimalMax("5.0")
    private Double valor;

    @NotNull @DecimalMin("0.0") @DecimalMax("100.0")
    private Double porcentaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    // <- Esto hace que Jackson no intente serializar los campos internos del proxy
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Estudiante estudiante;
}

