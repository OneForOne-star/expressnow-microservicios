package cl.duoc.expressnow.seguimiento;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del seguimiento contra H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguimientoControllerTest {

    @Autowired
    private MockMvc mvc;

    private long crear(long envioId, String codigo) throws Exception {
        String resp = mvc.perform(post("/api/v1/seguimientos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"envioId\":" + envioId + ",\"codigoSeguimiento\":\"" + codigo + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoActual").value("REGISTRADO"))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(resp.replaceAll("^\\{\"id\":(\\d+).*", "$1"));
    }

    @Test
    void flujoCompletoHastaEntrega() throws Exception {
        long id = crear(10, "EXN-ABCD2345");

        mvc.perform(post("/api/v1/seguimientos/{id}/eventos", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_RUTA\",\"descripcion\":\"Salió a reparto\","
                                + "\"ubicacion\":\"Santiago Centro\",\"latitud\":-33.4489,\"longitud\":-70.6693}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoActual").value("EN_RUTA"))
                .andExpect(jsonPath("$.ubicacionActual").value("Santiago Centro"));

        mvc.perform(post("/api/v1/seguimientos/{id}/eventos", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ENTREGADO\",\"descripcion\":\"Recibido por el destinatario\"}"))
                .andExpect(status().isCreated());

        // Estado final: no admite más eventos
        mvc.perform(post("/api/v1/seguimientos/{id}/eventos", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_RUTA\",\"descripcion\":\"Otra vez\"}"))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/v1/seguimientos/codigo/{codigo}", "exn-abcd2345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventos.length()").value(3));
    }

    @Test
    void duplicadosYValidaciones() throws Exception {
        crear(20, "EXN-ZZZZ9999");

        mvc.perform(post("/api/v1/seguimientos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"envioId\":20,\"codigoSeguimiento\":\"EXN-YYYY8888\"}"))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/v1/seguimientos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"envioId\":-1,\"codigoSeguimiento\":\"malo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.codigoSeguimiento").exists());

        mvc.perform(get("/api/v1/seguimientos/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarSeguimiento() throws Exception {
        long id = crear(30, "EXN-QQQQ7777");
        mvc.perform(delete("/api/v1/seguimientos/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/seguimientos/{id}", id)).andExpect(status().isNotFound());
    }
}
