package cl.duoc.expressnow.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos para registrar una dirección de un usuario.
 */
public record DireccionRequest(
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 120)
        String calle,

        @NotBlank(message = "El número es obligatorio")
        @Size(max = 10)
        String numero,

        @Size(max = 40)
        String depto,

        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 60)
        String comuna,

        @NotBlank(message = "La región es obligatoria")
        @Size(max = 60)
        String region,

        boolean principal) {
}
