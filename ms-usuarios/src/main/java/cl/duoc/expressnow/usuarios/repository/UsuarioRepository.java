package cl.duoc.expressnow.usuarios.repository;

import cl.duoc.expressnow.usuarios.model.TipoUsuario;
import cl.duoc.expressnow.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByTipo(TipoUsuario tipo);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRut(String rut);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
