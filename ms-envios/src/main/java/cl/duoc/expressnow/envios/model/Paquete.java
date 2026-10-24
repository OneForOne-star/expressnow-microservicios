package cl.duoc.expressnow.envios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Bulto que forma parte de un envío (ManyToOne).
 */
@Entity
@Table(name = "paquetes")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String descripcion;

    @Column(name = "peso_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "largo_cm", nullable = false)
    private Integer largoCm;

    @Column(name = "ancho_cm", nullable = false)
    private Integer anchoCm;

    @Column(name = "alto_cm", nullable = false)
    private Integer altoCm;

    @Column(nullable = false)
    private boolean fragil;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envio_id", nullable = false)
    private Envio envio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public Integer getLargoCm() { return largoCm; }
    public void setLargoCm(Integer largoCm) { this.largoCm = largoCm; }
    public Integer getAnchoCm() { return anchoCm; }
    public void setAnchoCm(Integer anchoCm) { this.anchoCm = anchoCm; }
    public Integer getAltoCm() { return altoCm; }
    public void setAltoCm(Integer altoCm) { this.altoCm = altoCm; }
    public boolean isFragil() { return fragil; }
    public void setFragil(boolean fragil) { this.fragil = fragil; }
    public Envio getEnvio() { return envio; }
    public void setEnvio(Envio envio) { this.envio = envio; }
}
