package nl.itqaanconsulting.servicedesk.ticket.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsAndReturnsTicket() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTicketJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("VPN access unavailable"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void changesTicketStatusAndFiltersTickets() throws Exception {
        UUID ticketId = createTicket();

        mockMvc.perform(patch("/api/tickets/{ticketId}/status", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(get("/api/tickets").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ticketId.toString()));
    }

    @Test
    void rejectsInvalidTicket() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Missing requester details",
                                  "requesterEmail": "not-an-email",
                                  "priority": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.title").exists())
                .andExpect(jsonPath("$.fieldErrors.requesterEmail").exists())
                .andExpect(jsonPath("$.fieldErrors.priority").exists());
    }

    @Test
    void returnsNotFoundForUnknownTicket() throws Exception {
        UUID ticketId = UUID.randomUUID();

        mockMvc.perform(get("/api/tickets/{ticketId}", ticketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket " + ticketId + " was not found"));
    }

    private UUID createTicket() throws Exception {
        String response = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTicketJson()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode ticket = objectMapper.readTree(response);
        return UUID.fromString(ticket.get("id").asText());
    }

    private String validTicketJson() {
        return """
                {
                  "title": "VPN access unavailable",
                  "description": "Remote employee cannot connect to the corporate VPN.",
                  "requesterEmail": "alex@example.com",
                  "priority": "HIGH"
                }
                """;
    }
}
