package cl.duoc.expressnow.envios;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de envíos contra H2. ms-transportistas no está levantado,
 * por lo que se verifica también el fallback del Circuit Breaker.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EnvioControllerTest {

    @Autowired
    private MockMvc mvc;

    private String envioJson(LocalDate fecha) {
        return """
                {
                  "clienteId": 1,
                  "direccionOrigen": "Av. Providencia 1234",
                  "comunaOrigen": "Providencia",
                  "direccionDestino": "Los Carrera 456",
                  "comunaDestino": "Concepcion",
                  "nombreDestinatario": "Juan Perez",
                  "telefonoDestinatario": "+56933334444",
                  "fechaRetiro": "%s",
                  "paquetes": [
                    {"descripcion": "Libros", "pesoKg": 2.4, "largoCm": 30, "anchoCm": 20, "altoCm": 15, "fragil": false}
                  ]
                }
                """.formatted(fecha);
    }

    private long crearEnvio() throws Exception {
        String resp = mvc.perform(post("/api/v1/envios").contentType(MediaType.APPLICATION_JSON)
                        .content(envioJson(LocalDate.now().plusDays(1))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(resp.replaceAll("^\\{\"id\":(\\d+).*", "$1"));
    }

    @Test
    void crearEnvioSinTransportistasQuedaPendiente() throws Exception {
        mvc.perform(post("/api/v1/envios").contentType(MediaType.APPLICATION_JSON)
                        .content(envioJson(LocalDate.now().plusDays(1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoSeguimiento").isString())
                .andExpect(jsonPath("$.estado").value("PENDIENTE_ASIGNACION"))
                // 2990 + 990 * 3 kg (2.4 redondeado hacia arriba)
                .andExpect(jsonPath("$.costo").value(5960.0))
                .andExpect(jsonPath("$.paquetes.length()").value(1));
    }

    @Test
    void reprogramarCancelarYEliminar() throws Exception {
        long id = crearEnvio();

        mvc.perform(patch("/api/v1/envios/{id}/reprogramar", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaRetiro\":\"" + LocalDate.now().plusDays(5) + "\",\"motivo\":\"No estaré en casa\"}"))
                .andExpect(status().isOk());

        // No se puede eliminar sin cancelar antes
        mvc.perform(delete("/api/v1/envios/{id}", id))
                .andExpect(status().isConflict());

        mvc.perform(patch("/api/v1/envios/{id}/cancelar", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Ya no lo necesito\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));

        mvc.perform(get("/api/v1/envios/{id}/historial", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

        mvc.perform(delete("/api/v1/envios/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/envios/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void transicionInvalidaDevuelve409() throws Exception {
        long id = crearEnvio();
        mvc.perform(patch("/api/v1/envios/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ENTREGADO\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void fechaPasadaYSinPaquetesDevuelve400() throws Exception {
        String invalido = envioJson(LocalDate.now().minusDays(3))
                .replaceAll("\"paquetes\": \\[[\\s\\S]*]", "\"paquetes\": []");
        mvc.perform(post("/api/v1/envios").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.fechaRetiro").exists())
                .andExpect(jsonPath("$.detalles.paquetes").exists());
    }
}
