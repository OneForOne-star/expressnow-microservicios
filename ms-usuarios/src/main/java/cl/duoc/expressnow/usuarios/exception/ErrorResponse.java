package cl.duoc.expressnow.usuarios.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato único de error que devuelven todos los endpoints.
 *
 * @param timestamp momento del error
 * @param status    código HTTP
 * @param error     descripción corta del código HTTP
 * @param mensaje   explicación legible del problema
 * @param path      ruta solicitada
 * @param detalles  errores por campo (solo en validaciones), puede ser null
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> detalles) {
}
