package com.ejemplo.Taller_AOP.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.Taller_AOP.modelo.Nota;

import java.util.List;

public interface RepositorioNota extends JpaRepository<Nota, Long> {
    List<Nota> findByEstudianteId(Long estudianteId);

    List<Nota> findByEstudianteIdAndMateria(Long estudianteId, String materia);
}