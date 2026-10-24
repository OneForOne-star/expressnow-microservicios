package cl.duoc.expressnow.envios.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaqueteRequest(
        @NotBlank(message = "La descripción del paquete es obligatoria")
        @Size(max = 120)
        String descripcion,

        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor a 0")
        @DecimalMax(value = "50", message = "El peso máximo por paquete es 50 kg")
        BigDecimal pesoKg,

        @NotNull(message = "El largo es obligatorio") @Positive @Max(200)
        Integer largoCm,

        @NotNull(message = "El ancho es obligatorio") @Positive @Max(200)
        Integer anchoCm,

        @NotNull(message = "El alto es obligatorio") @Positive @Max(200)
        Integer altoCm,

        boolean fragil) {
}
