package com.saicharan.faultmanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saicharan.faultmanagement.repository.FaultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FaultControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired FaultRepository repository;

    private static final String VALID_FAULT = """
            {
              "title": "Link down on uplink port",
              "description": "Gi0/1 lost carrier",
              "deviceName": "router-blr-01",
              "ipAddress": "10.0.0.1",
              "severity": "HIGH",
              "assignedTo": "noc-team"
            }
            """;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    private long createFault() throws Exception {
        String body = mvc.perform(post("/api/faults").contentType(MediaType.APPLICATION_JSON).content(VALID_FAULT))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = mapper.readTree(body);
        return node.get("id").asLong();
    }

    @Test
    void createReturns201WithOpenStatus() throws Exception {
        mvc.perform(post("/api/faults").contentType(MediaType.APPLICATION_JSON).content(VALID_FAULT))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.severity").value("HIGH"));
    }

    @Test
    void createWithMissingFieldsReturns400() throws Exception {
        mvc.perform(post("/api/faults").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details", hasSize(3)));
    }

    @Test
    void invalidIpReturns400() throws Exception {
        String bad = VALID_FAULT.replace("10.0.0.1", "999.1.1.1");
        mvc.perform(post("/api/faults").contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUnknownIdReturns404() throws Exception {
        mvc.perform(get("/api/faults/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Fault not found with id 9999"));
    }

    @Test
    void fullLifecycleOpenToClosed() throws Exception {
        long id = createFault();

        mvc.perform(patch("/api/faults/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mvc.perform(patch("/api/faults/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"RESOLVED\",\"resolutionNotes\":\"Replaced SFP module\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resolvedAt", notNullValue()))
                .andExpect(jsonPath("$.resolutionNotes").value("Replaced SFP module"));

        mvc.perform(patch("/api/faults/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CLOSED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void invalidTransitionReturns409() throws Exception {
        long id = createFault();
        mvc.perform(patch("/api/faults/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CLOSED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void filterBySeverityAndStatus() throws Exception {
        createFault();
        createFault();
        mvc.perform(get("/api/faults").param("severity", "HIGH").param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/faults").param("severity", "LOW"))
                .andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/api/faults").param("device", "ROUTER-BLR-01"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateAndDelete() throws Exception {
        long id = createFault();
        String updated = VALID_FAULT.replace("HIGH", "CRITICAL");
        mvc.perform(put("/api/faults/{id}", id).contentType(MediaType.APPLICATION_JSON).content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.severity").value("CRITICAL"));

        mvc.perform(delete("/api/faults/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/faults/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void summaryCountsFaults() throws Exception {
        createFault();
        mvc.perform(get("/api/faults/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.byStatus.OPEN").value(1))
                .andExpect(jsonPath("$.activeBySeverity.HIGH").value(1));
    }
}
