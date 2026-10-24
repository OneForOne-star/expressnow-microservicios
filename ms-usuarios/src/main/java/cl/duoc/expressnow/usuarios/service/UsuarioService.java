package cl.duoc.expressnow.usuarios.service;

import cl.duoc.expressnow.usuarios.dto.DireccionRequest;
import cl.duoc.expressnow.usuarios.dto.DireccionResponse;
import cl.duoc.expressnow.usuarios.dto.LoginRequest;
import cl.duoc.expressnow.usuarios.dto.LoginResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioRequest;
import cl.duoc.expressnow.usuarios.dto.UsuarioResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioUpdateRequest;
import cl.duoc.expressnow.usuarios.model.TipoUsuario;

import java.util.List;

/**
 * Lógica de negocio de usuarios. El controlador depende de esta interfaz, no de la implementación.
 */
public interface UsuarioService {

    List<UsuarioResponse> listar(TipoUsuario tipo);

    UsuarioResponse obtener(Long id);

    UsuarioResponse crear(UsuarioRequest request);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    void eliminar(Long id);

    List<DireccionResponse> listarDirecciones(Long usuarioId);

    DireccionResponse agregarDireccion(Long usuarioId, DireccionRequest request);

    void eliminarDireccion(Long usuarioId, Long direccionId);

    LoginResponse login(LoginRequest request);
}
