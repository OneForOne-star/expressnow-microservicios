package cl.duoc.expressnow.transportistas.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio o genera un
 * conflicto con el estado actual del recurso. Se traduce a HTTP 409.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
