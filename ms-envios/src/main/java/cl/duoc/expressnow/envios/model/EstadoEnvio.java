package cl.duoc.expressnow.envios.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados del envío y transiciones permitidas entre ellos.
 */
public enum EstadoEnvio {
    SOLICITADO,
    PENDIENTE_ASIGNACION,
    ASIGNADO,
    RETIRADO,
    EN_RUTA,
    ENTREGADO,
    CANCELADO;

    public boolean puedeCambiarA(EstadoEnvio destino) {
        return siguientes().contains(destino);
    }

    public Set<EstadoEnvio> siguientes() {
        return switch (this) {
            case SOLICITADO -> EnumSet.of(PENDIENTE_ASIGNACION, ASIGNADO, CANCELADO);
            case PENDIENTE_ASIGNACION -> EnumSet.of(ASIGNADO, CANCELADO);
            case ASIGNADO -> EnumSet.of(RETIRADO, PENDIENTE_ASIGNACION, CANCELADO);
            case RETIRADO -> EnumSet.of(EN_RUTA);
            case EN_RUTA -> EnumSet.of(ENTREGADO);
            case ENTREGADO, CANCELADO -> EnumSet.noneOf(EstadoEnvio.class);
        };
    }

    /** Mientras el paquete no ha sido retirado, el cliente puede reprogramar, editar o cancelar. */
    public boolean esModificablePorCliente() {
        return this == SOLICITADO || this == PENDIENTE_ASIGNACION || this == ASIGNADO;
    }
}
