package cl.duoc.expressnow.usuarios.exception;

/**
 * Email o contraseña incorrectos. Se traduce a HTTP 401.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Email o contraseña incorrectos");
    }
}
