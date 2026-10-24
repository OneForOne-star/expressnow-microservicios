package cl.duoc.expressnow.usuarios.dto;

import cl.duoc.expressnow.usuarios.model.TipoUsuario;

/**
 * Resultado de un login exitoso. En la etapa de despliegue este servicio
 * emitirá además un token JWT que validará el API Gateway.
 */
public record LoginResponse(
        Long usuarioId,
        String nombreCompleto,
        String email,
        TipoUsuario tipo,
        String mensaje) {
}
