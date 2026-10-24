package cl.duoc.expressnow.seguimiento.repository;

import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import cl.duoc.expressnow.seguimiento.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    Optional<Seguimiento> findByCodigoSeguimientoIgnoreCase(String codigo);

    Optional<Seguimiento> findByEnvioId(Long envioId);

    List<Seguimiento> findByEstadoActual(EstadoSeguimiento estado);

    boolean existsByEnvioId(Long envioId);

    boolean existsByCodigoSeguimientoIgnoreCase(String codigo);
}
