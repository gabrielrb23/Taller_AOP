package com.ejemplo.Taller_AOP.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejemplo.Taller_AOP.modelo.Estudiante;

import java.util.Optional;

public interface RepositorioEstudiante extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCorreo(String correo);
}