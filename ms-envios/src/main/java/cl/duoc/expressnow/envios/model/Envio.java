package cl.duoc.expressnow.envios.model;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orden de envío. Tiene muchos paquetes y un historial de estados (OneToMany).
 */
@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 20)
    private String codigoSeguimiento;

    /** Id del cliente en ms-usuarios (referencia lógica). */
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "direccion_origen", nullable = false, length = 160)
    private String direccionOrigen;

    @Column(name = "comuna_origen", nullable = false, length = 60)
    private String comunaOrigen;

    @Column(name = "direccion_destino", nullable = false, length = 160)
    private String direccionDestino;

    @Column(name = "comuna_destino", nullable = false, length = 60)
    private String comunaDestino;

    @Column(name = "nombre_destinatario", nullable = false, length = 120)
    private String nombreDestinatario;

    @Column(name = "telefono_destinatario", length = 20)
    private String telefonoDestinatario;

    @Column(name = "fecha_retiro", nullable = false)
    private LocalDate fechaRetiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoEnvio estado;

    /** Id del transportista en ms-transportistas, null mientras no se asigne. */
    @Column(name = "transportista_id")
    private Long transportistaId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paquete> paquetes = new ArrayList<>();

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC, id ASC")
    private List<HistorialEstado> historial = new ArrayList<>();

    @PrePersist
    void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    @PreUpdate
    void alActualizar() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void agregarPaquete(Paquete paquete) {
        paquetes.add(paquete);
        paquete.setEnvio(this);
    }

    /** Cambia el estado y deja el registro en el historial. */
    public void cambiarEstado(EstadoEnvio nuevo, String comentario) {
        HistorialEstado h = new HistorialEstado();
        h.setEstadoAnterior(this.estado);
        h.setEstadoNuevo(nuevo);
        h.setComentario(comentario);
        h.setEnvio(this);
        historial.add(h);
        this.estado = nuevo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getDireccionOrigen() { return direccionOrigen; }
    public void setDireccionOrigen(String direccionOrigen) { this.direccionOrigen = direccionOrigen; }
    public String getComunaOrigen() { return comunaOrigen; }
    public void setComunaOrigen(String comunaOrigen) { this.comunaOrigen = comunaOrigen; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public String getComunaDestino() { return comunaDestino; }
    public void setComunaDestino(String comunaDestino) { this.comunaDestino = comunaDestino; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public void setNombreDestinatario(String nombreDestinatario) { this.nombreDestinatario = nombreDestinatario; }
    public String getTelefonoDestinatario() { return telefonoDestinatario; }
    public void setTelefonoDestinatario(String telefonoDestinatario) { this.telefonoDestinatario = telefonoDestinatario; }
    public LocalDate getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDate fechaRetiro) { this.fechaRetiro = fechaRetiro; }
    public EstadoEnvio getEstado() { return estado; }
    public void setEstado(EstadoEnvio estado) { this.estado = estado; }
    public Long getTransportistaId() { return transportistaId; }
    public void setTransportistaId(Long transportistaId) { this.transportistaId = transportistaId; }
    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public List<Paquete> getPaquetes() { return paquetes; }
    public List<HistorialEstado> getHistorial() { return historial; }
}
