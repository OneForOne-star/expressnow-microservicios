package cl.duoc.expressnow.transportistas.controller;

import cl.duoc.expressnow.transportistas.dto.AsignacionRequest;
import cl.duoc.expressnow.transportistas.dto.AsignacionResponse;
import cl.duoc.expressnow.transportistas.service.AsignacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asignaciones")
@Tag(name = "Asignaciones", description = "Asignación de envíos a transportistas")
public class AsignacionController {

    private final AsignacionService service;

    public AsignacionController(AsignacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Asigna un envío al primer transportista disponible (lo invoca ms-envios)")
    public ResponseEntity<AsignacionResponse> asignar(@Valid @RequestBody AsignacionRequest request) {
        AsignacionResponse creada = service.asignar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    @Operation(summary = "Lista asignaciones, opcionalmente de un transportista")
    public ResponseEntity<List<AsignacionResponse>> listar(@RequestParam(required = false) Long transportistaId) {
        return ResponseEntity.ok(service.listar(transportistaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una asignación por id")
    public ResponseEntity<AsignacionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PatchMapping("/{id}/completar")
    @Operation(summary = "Marca la asignación como completada")
    public ResponseEntity<AsignacionResponse> completar(@PathVariable Long id) {
        return ResponseEntity.ok(service.completar(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela la asignación")
    public ResponseEntity<AsignacionResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
