package cl.duoc.expressnow.transportistas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransportistaRequest(
        @Positive(message = "El usuarioId debe ser positivo")
        Long usuarioId,

        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9")
        String rut,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @Pattern(regexp = "^\\+?\\d{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La comuna base es obligatoria")
        @Size(max = 60)
        String comunaBase) {
}
