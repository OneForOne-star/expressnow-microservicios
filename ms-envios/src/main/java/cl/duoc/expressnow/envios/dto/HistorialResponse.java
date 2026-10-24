package cl.duoc.expressnow.envios.dto;

import cl.duoc.expressnow.envios.model.EstadoEnvio;
import cl.duoc.expressnow.envios.model.HistorialEstado;

import java.time.LocalDateTime;

public record HistorialResponse(
        Long id,
        EstadoEnvio estadoAnterior,
        EstadoEnvio estadoNuevo,
        String comentario,
        LocalDateTime fecha) {

    public static HistorialResponse desde(HistorialEstado h) {
        return new HistorialResponse(h.getId(), h.getEstadoAnterior(), h.getEstadoNuevo(),
                h.getComentario(), h.getFecha());
    }
}
