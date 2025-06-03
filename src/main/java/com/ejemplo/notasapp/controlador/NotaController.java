package com.ejemplo.notasapp.controlador;

import com.ejemplo.notasapp.modelo.Nota;
import com.ejemplo.notasapp.repositorio.RepositorioNota;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final RepositorioNota repoNota;

    public NotaController(RepositorioNota repoNota) {
        this.repoNota = repoNota;
    }

    // Listar todas, o filtrar por estudianteId
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Nota> listar(
            @RequestParam(name = "estudianteId", required = false) Long estudianteId) {
        if (estudianteId != null) {
            return repoNota.findByEstudianteId(estudianteId);
        }
        return repoNota.findAll();
    }

    // Obtener por ID
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Nota> obtener(@PathVariable Long id) {
        return repoNota.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva nota, con manejo de duplicados
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Nota> crear(@Valid @RequestBody Nota n) {
        try {
            Nota guardada = repoNota.save(n);
            URI uri = URI.create("/api/notas/" + guardada.getId());
            return ResponseEntity.created(uri).body(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya tienes registrada una nota de '"
                            + n.getMateria()
                            + "' para este estudiante."
            );
        }
    }

    // Actualizar existente, también capturando duplicados
    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Nota> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Nota n) {
        return repoNota.findById(id)
                .map(orig -> {
                    n.setId(id);
                    try {
                        Nota updated = repoNota.save(n);
                        return ResponseEntity.ok(updated);
                    } catch (DataIntegrityViolationException ex) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Ya tienes registrada una nota de '"
                                        + n.getMateria()
                                        + "' para este estudiante."
                        );
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repoNota.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repoNota.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Promedio ponderado
    @GetMapping(path = "/promedio/{estudianteId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> promedio(@PathVariable Long estudianteId) {
        List<Nota> notas = repoNota.findByEstudianteId(estudianteId);
        double prom = notas.stream()
                .mapToDouble(n -> n.getValor() * n.getPorcentaje() / 100.0)
                .sum();
        return ResponseEntity.ok(prom);
    }
}
