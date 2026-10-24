package cl.duoc.expressnow.usuarios.service;

import cl.duoc.expressnow.usuarios.dto.DireccionRequest;
import cl.duoc.expressnow.usuarios.dto.DireccionResponse;
import cl.duoc.expressnow.usuarios.dto.LoginRequest;
import cl.duoc.expressnow.usuarios.dto.LoginResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioRequest;
import cl.duoc.expressnow.usuarios.dto.UsuarioResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioUpdateRequest;
import cl.duoc.expressnow.usuarios.exception.CredencialesInvalidasException;
import cl.duoc.expressnow.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.expressnow.usuarios.exception.ReglaNegocioException;
import cl.duoc.expressnow.usuarios.model.Direccion;
import cl.duoc.expressnow.usuarios.model.TipoUsuario;
import cl.duoc.expressnow.usuarios.model.Usuario;
import cl.duoc.expressnow.usuarios.repository.DireccionRepository;
import cl.duoc.expressnow.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final PasswordEncoder passwordEncoder;

    /** Inyección de dependencias por constructor. */
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              DireccionRepository direccionRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponse> listar(TipoUsuario tipo) {
        List<Usuario> usuarios = (tipo == null) ? usuarioRepository.findAll() : usuarioRepository.findByTipo(tipo);
        return usuarios.stream().map(UsuarioResponse::desde).toList();
    }

    @Override
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ReglaNegocioException("Ya existe un usuario con el email " + request.email());
        }
        if (usuarioRepository.existsByRut(request.rut())) {
            throw new ReglaNegocioException("Ya existe un usuario con el RUT " + request.rut());
        }
        Usuario usuario = new Usuario();
        usuario.setRut(request.rut());
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email().toLowerCase());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setTelefono(request.telefono());
        usuario.setTipo(request.tipo());
        usuario.setActivo(true);
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscar(id);
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ReglaNegocioException("El email " + request.email() + " ya está en uso por otro usuario");
        }
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email().toLowerCase());
        usuario.setTelefono(request.telefono());
        usuario.setActivo(request.activo());
        return UsuarioResponse.desde(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(buscar(id));
    }

    @Override
    public List<DireccionResponse> listarDirecciones(Long usuarioId) {
        buscar(usuarioId);
        return direccionRepository.findByUsuarioId(usuarioId).stream().map(DireccionResponse::desde).toList();
    }

    @Override
    @Transactional
    public DireccionResponse agregarDireccion(Long usuarioId, DireccionRequest request) {
        Usuario usuario = buscar(usuarioId);
        // Solo puede haber una dirección principal por usuario
        if (request.principal()) {
            usuario.getDirecciones().forEach(d -> d.setPrincipal(false));
        }
        Direccion direccion = new Direccion();
        direccion.setCalle(request.calle());
        direccion.setNumero(request.numero());
        direccion.setDepto(request.depto());
        direccion.setComuna(request.comuna());
        direccion.setRegion(request.region());
        direccion.setPrincipal(request.principal() || usuario.getDirecciones().isEmpty());
        usuario.agregarDireccion(direccion);
        return DireccionResponse.desde(direccionRepository.save(direccion));
    }

    @Override
    @Transactional
    public void eliminarDireccion(Long usuarioId, Long direccionId) {
        Usuario usuario = buscar(usuarioId);
        Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La dirección " + direccionId + " no existe para el usuario " + usuarioId));
        usuario.quitarDireccion(direccion);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .filter(Usuario::isActivo)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);
        return new LoginResponse(usuario.getId(), usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getEmail(), usuario.getTipo(), "Login exitoso");
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }
}
