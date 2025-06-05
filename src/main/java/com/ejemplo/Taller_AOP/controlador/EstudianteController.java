package com.ejemplo.Taller_AOP.controlador;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ejemplo.Taller_AOP.modelo.Estudiante;
import com.ejemplo.Taller_AOP.repositorio.RepositorioEstudiante;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final RepositorioEstudiante repo;

    public EstudianteController(RepositorioEstudiante repo) {
        this.repo = repo;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Estudiante> listar() {
        return repo.findAll();
    }
    // — Obtener una por ID —
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Estudiante> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody Estudiante e) {
        if (repo.findByCorreo(e.getCorreo()).isPresent()) {
            throw new Error("El correo electronico ya esta registrado: " + e.getCorreo());
        }
        Estudiante guardado = repo.save(e);
        URI uri = URI.create("/api/estudiantes/" + guardado.getId());
        return ResponseEntity.created(uri).body(guardado);
    }

    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Estudiante> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Estudiante e) {
        if (repo.findByCorreo(e.getCorreo()).isPresent() && !repo.findById(id).map(Estudiante::getCorreo).orElse("").equals(e.getCorreo())) {
            throw new Error("El correo electrónico ya esta registrado: " + e.getCorreo());
        }
        return repo.findById(id)
                .map(orig -> {
                    e.setId(id);
                    e.setNotas(orig.getNotas());
                    return ResponseEntity.ok(repo.save(e));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
