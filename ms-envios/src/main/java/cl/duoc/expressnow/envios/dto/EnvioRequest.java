package cl.duoc.expressnow.envios.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Solicitud de retiro de paquetes (crea un envío).
 */
public record EnvioRequest(
        @NotNull(message = "El clienteId es obligatorio")
        @Positive(message = "El clienteId debe ser positivo")
        Long clienteId,

        @NotBlank(message = "La dirección de origen es obligatoria")
        @Size(max = 160)
        String direccionOrigen,

        @NotBlank(message = "La comuna de origen es obligatoria")
        @Size(max = 60)
        String comunaOrigen,

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
        String telefonoDestinatario,

        @NotNull(message = "La fecha de retiro es obligatoria (formato AAAA-MM-DD)")
        @FutureOrPresent(message = "La fecha de retiro no puede estar en el pasado")
        LocalDate fechaRetiro,

        @NotEmpty(message = "El envío debe tener al menos un paquete")
        List<@Valid PaqueteRequest> paquetes) {
}
