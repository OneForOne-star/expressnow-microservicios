package cl.duoc.expressnow.envios.dto;

import cl.duoc.expressnow.envios.model.EstadoEnvio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cambio de estado operativo (lo usan transportistas o soporte).
 */
public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoEnvio estado,

        @Size(max = 200)
        String comentario) {
}
