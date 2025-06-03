package com.ejemplo.notasapp.repositorio;

import com.ejemplo.notasapp.modelo.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioEstudiante extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCorreo(String correo);
}