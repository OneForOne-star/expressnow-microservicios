package cl.duoc.expressnow.envios.controller;

import cl.duoc.expressnow.envios.dto.CambioEstadoRequest;
import cl.duoc.expressnow.envios.dto.CancelarRequest;
import cl.duoc.expressnow.envios.dto.EnvioRequest;
import cl.duoc.expressnow.envios.dto.EnvioResponse;
import cl.duoc.expressnow.envios.dto.EnvioUpdateRequest;
import cl.duoc.expressnow.envios.dto.HistorialResponse;
import cl.duoc.expressnow.envios.dto.ReprogramarRequest;
import cl.duoc.expressnow.envios.model.EstadoEnvio;
import cl.duoc.expressnow.envios.service.EnvioService;
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
@RequestMapping("/api/v1/envios")
@Tag(name = "Envíos", description = "Solicitud de retiro, reprogramación, cancelación e historial")
public class EnvioController {

    private final EnvioService service;

    public EnvioController(EnvioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista envíos, con filtros opcionales por cliente y estado")
    public ResponseEntity<List<EnvioResponse>> listar(@RequestParam(required = false) Long clienteId,
                                                      @RequestParam(required = false) EstadoEnvio estado) {
        return ResponseEntity.ok(service.listar(clienteId, estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un envío por id")
    public ResponseEntity<EnvioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtiene un envío por su código de seguimiento")
    public ResponseEntity<EnvioResponse> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crea una solicitud de retiro y pide un transportista")
    public ResponseEntity<EnvioResponse> crear(@Valid @RequestBody EnvioRequest request) {
        EnvioResponse creado = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza los datos de destino (solo antes del retiro)")
    public ResponseEntity<EnvioResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody EnvioUpdateRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Cambia la fecha de retiro")
    public ResponseEntity<EnvioResponse> reprogramar(@PathVariable Long id,
                                                     @Valid @RequestBody ReprogramarRequest request) {
        return ResponseEntity.ok(service.reprogramar(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela el envío (solo antes del retiro)")
    public ResponseEntity<EnvioResponse> cancelar(@PathVariable Long id,
                                                  @Valid @RequestBody CancelarRequest request) {
        return ResponseEntity.ok(service.cancelar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambia el estado operativo respetando las transiciones válidas")
    public ResponseEntity<EnvioResponse> cambiarEstado(@PathVariable Long id,
                                                       @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(service.cambiarEstado(id, request));
    }

    @PostMapping("/{id}/asignacion")
    @Operation(summary = "Reintenta asignar transportista a un envío PENDIENTE_ASIGNACION")
    public ResponseEntity<EnvioResponse> reintentarAsignacion(@PathVariable Long id) {
        return ResponseEntity.ok(service.reintentarAsignacion(id));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "Historial de cambios de estado del envío")
    public ResponseEntity<List<HistorialResponse>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(service.historial(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un envío cancelado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
