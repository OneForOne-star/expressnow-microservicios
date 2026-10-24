package cl.duoc.expressnow.transportistas.repository;

import cl.duoc.expressnow.transportistas.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    List<Transportista> findByDisponible(boolean disponible);

    List<Transportista> findByComunaBaseIgnoreCase(String comuna);

    List<Transportista> findByDisponibleAndComunaBaseIgnoreCase(boolean disponible, String comuna);

    /** Primer transportista disponible de la comuna (el más antiguo primero). */
    Optional<Transportista> findFirstByDisponibleTrueAndComunaBaseIgnoreCaseOrderByIdAsc(String comuna);

    /** Primer transportista disponible de cualquier comuna. */
    Optional<Transportista> findFirstByDisponibleTrueOrderByIdAsc();

    boolean existsByRut(String rut);
}
