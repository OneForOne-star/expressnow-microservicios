package cl.duoc.expressnow.usuarios.controller;

import cl.duoc.expressnow.usuarios.dto.DireccionRequest;
import cl.duoc.expressnow.usuarios.dto.DireccionResponse;
import cl.duoc.expressnow.usuarios.dto.LoginRequest;
import cl.duoc.expressnow.usuarios.dto.LoginResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioRequest;
import cl.duoc.expressnow.usuarios.dto.UsuarioResponse;
import cl.duoc.expressnow.usuarios.dto.UsuarioUpdateRequest;
import cl.duoc.expressnow.usuarios.model.TipoUsuario;
import cl.duoc.expressnow.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios, direcciones y login")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Lista usuarios, opcionalmente filtrados por tipo")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(required = false) TipoUsuario tipo) {
        return ResponseEntity.ok(usuarioService.listar(tipo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por id")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Registra un cliente, transportista o usuario de soporte")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse creado = usuarioService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza el perfil de un usuario")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario y sus direcciones")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/direcciones")
    @Operation(summary = "Lista las direcciones de un usuario")
    public ResponseEntity<List<DireccionResponse>> listarDirecciones(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.listarDirecciones(id));
    }

    @PostMapping("/{id}/direcciones")
    @Operation(summary = "Agrega una dirección a un usuario")
    public ResponseEntity<DireccionResponse> agregarDireccion(@PathVariable Long id,
                                                              @Valid @RequestBody DireccionRequest request) {
        DireccionResponse creada = usuarioService.agregarDireccion(id, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{direccionId}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @DeleteMapping("/{id}/direcciones/{direccionId}")
    @Operation(summary = "Elimina una dirección de un usuario")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id, @PathVariable Long direccionId) {
        usuarioService.eliminarDireccion(id, direccionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Valida email y contraseña")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}
