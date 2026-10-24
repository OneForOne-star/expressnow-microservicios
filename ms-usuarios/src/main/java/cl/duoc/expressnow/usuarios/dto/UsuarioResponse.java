package cl.duoc.expressnow.usuarios.dto;

import cl.duoc.expressnow.usuarios.model.TipoUsuario;
import cl.duoc.expressnow.usuarios.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representación pública del usuario (sin el hash de la contraseña).
 */
public record UsuarioResponse(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String email,
        String telefono,
        TipoUsuario tipo,
        boolean activo,
        LocalDateTime fechaRegistro,
        List<DireccionResponse> direcciones) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRut(), u.getNombre(), u.getApellido(), u.getEmail(),
                u.getTelefono(), u.getTipo(), u.isActivo(), u.getFechaRegistro(),
                u.getDirecciones().stream().map(DireccionResponse::desde).toList());
    }
}
