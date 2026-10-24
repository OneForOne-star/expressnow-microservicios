package cl.duoc.expressnow.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corrección de la descripción o ubicación de un evento (no cambia su estado).
 */
public record EventoUpdateRequest(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255)
        String descripcion,

        @Size(max = 160)
        String ubicacion) {
}
