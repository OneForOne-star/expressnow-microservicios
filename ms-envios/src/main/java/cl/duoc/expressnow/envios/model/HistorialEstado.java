package cl.duoc.expressnow.envios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Registro de cada cambio de estado de un envío (ManyToOne).
 */
@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 25)
    private EstadoEnvio estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 25)
    private EstadoEnvio estadoNuevo;

    @Column(length = 255)
    private String comentario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envio_id", nullable = false)
    private Envio envio;

    @PrePersist
    void alCrear() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public EstadoEnvio getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoEnvio estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public EstadoEnvio getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoEnvio estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFecha() { return fecha; }
    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }
}
