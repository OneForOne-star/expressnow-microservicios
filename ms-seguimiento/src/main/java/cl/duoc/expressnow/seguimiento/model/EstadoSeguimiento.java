package cl.duoc.expressnow.seguimiento.model;

/**
 * Estados que se informan al cliente en el seguimiento.
 */
public enum EstadoSeguimiento {
    REGISTRADO,
    RETIRADO,
    EN_BODEGA,
    EN_RUTA,
    INTENTO_FALLIDO,
    ENTREGADO,
    CANCELADO;

    /** ENTREGADO y CANCELADO son estados finales: no admiten más eventos. */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
