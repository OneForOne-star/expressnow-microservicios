package cl.duoc.expressnow.transportistas.controller;

import cl.duoc.expressnow.transportistas.dto.DisponibilidadRequest;
import cl.duoc.expressnow.transportistas.dto.TransportistaRequest;
import cl.duoc.expressnow.transportistas.dto.TransportistaResponse;
import cl.duoc.expressnow.transportistas.dto.VehiculoRequest;
import cl.duoc.expressnow.transportistas.dto.VehiculoResponse;
import cl.duoc.expressnow.transportistas.service.TransportistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/transportistas")
@Tag(name = "Transportistas", description = "CRUD de transportistas y sus vehículos")
public class TransportistaController {

    private final TransportistaService service;

    public TransportistaController(TransportistaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista transportistas, con filtros opcionales por disponibilidad y comuna")
    public ResponseEntity<List<TransportistaResponse>> listar(@RequestParam(required = false) Boolean disponible,
                                                              @RequestParam(required = false) String comuna) {
        return ResponseEntity.ok(service.listar(disponible, comuna));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un transportista por id")
    public ResponseEntity<TransportistaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Registra un transportista")
    public ResponseEntity<TransportistaResponse> crear(@Valid @RequestBody TransportistaRequest request) {
        TransportistaResponse creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza los datos de un transportista")
    public ResponseEntity<TransportistaResponse> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody TransportistaRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/disponibilidad")
    @Operation(summary = "Marca al transportista como disponible o no disponible")
    public ResponseEntity<TransportistaResponse> cambiarDisponibilidad(@PathVariable Long id,
                                                                       @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(service.cambiarDisponibilidad(id, request.disponible()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un transportista sin asignaciones activas")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/vehiculos")
    @Operation(summary = "Lista los vehículos de un transportista")
    public ResponseEntity<List<VehiculoResponse>> listarVehiculos(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarVehiculos(id));
    }

    @PostMapping("/{id}/vehiculos")
    @Operation(summary = "Agrega un vehículo a un transportista")
    public ResponseEntity<VehiculoResponse> agregarVehiculo(@PathVariable Long id,
                                                            @Valid @RequestBody VehiculoRequest request) {
        VehiculoResponse creado = service.agregarVehiculo(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{vehiculoId}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @DeleteMapping("/{id}/vehiculos/{vehiculoId}")
    @Operation(summary = "Elimina un vehículo de un transportista")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id, @PathVariable Long vehiculoId) {
        service.eliminarVehiculo(id, vehiculoId);
        return ResponseEntity.noContent().build();
    }
}
