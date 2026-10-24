package cl.duoc.expressnow.envios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarRequest(
        @NotBlank(message = "El motivo de cancelación es obligatorio")
        @Size(max = 200)
        String motivo) {
}
