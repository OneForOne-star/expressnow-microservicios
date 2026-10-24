package cl.duoc.expressnow.seguimiento.controller;

import cl.duoc.expressnow.seguimiento.dto.EventoRequest;
import cl.duoc.expressnow.seguimiento.dto.EventoResponse;
import cl.duoc.expressnow.seguimiento.dto.EventoUpdateRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoResponse;
import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import cl.duoc.expressnow.seguimiento.service.SeguimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/seguimientos")
@Tag(name = "Seguimiento", description = "Estado en tiempo real y eventos de cada envío")
public class SeguimientoController {

    private final SeguimientoService service;

    public SeguimientoController(SeguimientoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista seguimientos, opcionalmente por estado actual")
    public ResponseEntity<List<SeguimientoResponse>> listar(@RequestParam(required = false) EstadoSeguimiento estado) {
        return ResponseEntity.ok(service.listar(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un seguimiento por id")
    public ResponseEntity<SeguimientoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Tracking público: consulta por código de seguimiento")
    public ResponseEntity<SeguimientoResponse> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @GetMapping("/envio/{envioId}")
    @Operation(summary = "Obtiene el seguimiento de un envío")
    public ResponseEntity<SeguimientoResponse> obtenerPorEnvio(@PathVariable Long envioId) {
        return ResponseEntity.ok(service.obtenerPorEnvio(envioId));
    }

    @PostMapping
    @Operation(summary = "Abre el seguimiento de un envío")
    public ResponseEntity<SeguimientoResponse> crear(@Valid @RequestBody SeguimientoRequest request) {
        SeguimientoResponse creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un seguimiento y sus eventos")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/eventos")
    @Operation(summary = "Línea de tiempo de eventos del envío")
    public ResponseEntity<List<EventoResponse>> listarEventos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarEventos(id));
    }

    @PostMapping("/{id}/eventos")
    @Operation(summary = "Registra un cambio de estado con ubicación (lo usa el transportista)")
    public ResponseEntity<SeguimientoResponse> registrarEvento(@PathVariable Long id,
                                                               @Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(201).body(service.registrarEvento(id, request));
    }

    @PutMapping("/{id}/eventos/{eventoId}")
    @Operation(summary = "Corrige la descripción o ubicación de un evento")
    public ResponseEntity<EventoResponse> corregirEvento(@PathVariable Long id, @PathVariable Long eventoId,
                                                         @Valid @RequestBody EventoUpdateRequest request) {
        return ResponseEntity.ok(service.corregirEvento(id, eventoId, request));
    }

    @DeleteMapping("/{id}/eventos/{eventoId}")
    @Operation(summary = "Elimina un evento registrado por error")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id, @PathVariable Long eventoId) {
        service.eliminarEvento(id, eventoId);
        return ResponseEntity.noContent().build();
    }
}
