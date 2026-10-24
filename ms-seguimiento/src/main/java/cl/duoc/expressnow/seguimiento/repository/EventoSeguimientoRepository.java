package cl.duoc.expressnow.seguimiento.repository;

import cl.duoc.expressnow.seguimiento.model.EventoSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoSeguimientoRepository extends JpaRepository<EventoSeguimiento, Long> {

    List<EventoSeguimiento> findBySeguimientoIdOrderByFechaEventoAscIdAsc(Long seguimientoId);

    Optional<EventoSeguimiento> findByIdAndSeguimientoId(Long id, Long seguimientoId);
}
