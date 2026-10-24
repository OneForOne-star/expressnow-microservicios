package cl.duoc.expressnow.seguimiento.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seguimiento de un envío. Tiene muchos eventos de seguimiento (OneToMany).
 */
@Entity
@Table(name = "seguimientos")
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id del envío en ms-envios (referencia lógica). */
    @Column(name = "envio_id", nullable = false, unique = true)
    private Long envioId;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual", nullable = false, length = 20)
    private EstadoSeguimiento estadoActual;

    @Column(name = "ubicacion_actual", length = 160)
    private String ubicacionActual;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "seguimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEvento ASC, id ASC")
    private List<EventoSeguimiento> eventos = new ArrayList<>();

    @PrePersist
    void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    /** Agrega el evento y actualiza el estado y la ubicación actuales. */
    public void registrarEvento(EventoSeguimiento evento) {
        eventos.add(evento);
        evento.setSeguimiento(this);
        this.estadoActual = evento.getEstado();
        if (evento.getUbicacion() != null) {
            this.ubicacionActual = evento.getUbicacion();
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void quitarEvento(EventoSeguimiento evento) {
        eventos.remove(evento);
        evento.setSeguimiento(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnvioId() { return envioId; }
    public void setEnvioId(Long envioId) { this.envioId = envioId; }
    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }
    public EstadoSeguimiento getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoSeguimiento estadoActual) { this.estadoActual = estadoActual; }
    public String getUbicacionActual() { return ubicacionActual; }
    public void setUbicacionActual(String ubicacionActual) { this.ubicacionActual = ubicacionActual; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public List<EventoSeguimiento> getEventos() { return eventos; }
}
