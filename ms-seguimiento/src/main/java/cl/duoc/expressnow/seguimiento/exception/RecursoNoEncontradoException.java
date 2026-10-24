package cl.duoc.expressnow.seguimiento.exception;

/**
 * Se lanza cuando un recurso solicitado no existe. Se traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " con id " + id + " no existe");
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
