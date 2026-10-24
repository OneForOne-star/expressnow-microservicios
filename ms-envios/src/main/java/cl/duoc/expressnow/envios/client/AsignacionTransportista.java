package cl.duoc.expressnow.envios.client;

/**
 * Respuesta de ms-transportistas a una solicitud de asignación
 * (solo los campos que ms-envios necesita).
 */
public record AsignacionTransportista(
        Long id,
        Long envioId,
        Long transportistaId,
        String transportistaNombre,
        String estado) {
}
