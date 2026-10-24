package cl.duoc.expressnow.transportistas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transportista de la flota. Tiene muchos vehículos y muchas asignaciones (OneToMany).
 */
@Entity
@Table(name = "transportistas")
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id del usuario en ms-usuarios (referencia lógica, sin FK entre bases distintas). */
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 20)
    private String telefono;

    @Column(name = "comuna_base", nullable = false, length = 60)
    private String comunaBase;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "transportista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehiculo> vehiculos = new ArrayList<>();

    @OneToMany(mappedBy = "transportista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();

    @PrePersist
    void alCrear() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
        vehiculo.setTransportista(this);
    }

    public void quitarVehiculo(Vehiculo vehiculo) {
        vehiculos.remove(vehiculo);
        vehiculo.setTransportista(null);
    }

    public void agregarAsignacion(Asignacion asignacion) {
        asignaciones.add(asignacion);
        asignacion.setTransportista(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getComunaBase() { return comunaBase; }
    public void setComunaBase(String comunaBase) { this.comunaBase = comunaBase; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public List<Vehiculo> getVehiculos() { return vehiculos; }
    public List<Asignacion> getAsignaciones() { return asignaciones; }
}
