package cl.duoc.expressnow.transportistas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Solicitud de asignación que envía ms-envios al crear un envío.
 */
public record AsignacionRequest(
        @NotNull(message = "El envioId es obligatorio")
        @Positive(message = "El envioId debe ser positivo")
        Long envioId,

        @NotBlank(message = "La comuna de retiro es obligatoria")
        String comuna) {
}
