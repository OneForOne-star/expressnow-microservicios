package cl.duoc.expressnow.seguimiento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Abre el seguimiento de un envío recién creado en ms-envios.
 */
public record SeguimientoRequest(
        @NotNull(message = "El envioId es obligatorio")
        @Positive(message = "El envioId debe ser positivo")
        Long envioId,

        @NotBlank(message = "El código de seguimiento es obligatorio")
        @Pattern(regexp = "^EXN-[A-Z0-9]{8}$", message = "El código debe tener el formato EXN-XXXXXXXX")
        String codigoSeguimiento,

        String ubicacion) {
}
