package cl.duoc.expressnow.envios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del destino que el cliente puede corregir antes del retiro.
 */
public record EnvioUpdateRequest(
        @NotBlank(message = "La dirección de destino es obligatoria")
        @Size(max = 160)
        String direccionDestino,

        @NotBlank(message = "La comuna de destino es obligatoria")
        @Size(max = 60)
        String comunaDestino,

        @NotBlank(message = "El nombre del destinatario es obligatorio")
        @Size(max = 120)
        String nombreDestinatario,

        @Pattern(regexp = "^\\+?\\d{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
        String telefonoDestinatario) {
}
