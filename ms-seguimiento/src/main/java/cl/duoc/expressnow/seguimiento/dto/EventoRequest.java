package cl.duoc.expressnow.seguimiento.dto;

import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cambio de estado informado por el transportista, con ubicación GPS opcional.
 */
public record EventoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoSeguimiento estado,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255)
        String descripcion,

        @Size(max = 160)
        String ubicacion,

        @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
        @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
        Double latitud,

        @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
        @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
        Double longitud) {
}
