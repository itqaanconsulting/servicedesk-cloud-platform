package nl.itqaanconsulting.servicedesk.ticket.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TechnicianClientTest {

    @Test
    void sendsTicketIdAsIdempotencyKey() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://technician-service");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID ticketId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        server.expect(requestTo("http://technician-service/api/technicians/reservations?skill=NETWORKING"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Idempotency-Key", ticketId.toString()))
                .andRespond(withSuccess("""
                        {"id": "%s", "name": "Samira de Vries", "email": "samira@example.com"}
                        """.formatted(technicianId), MediaType.APPLICATION_JSON));

        Optional<TechnicianReservation> reservation = new TechnicianClient(builder.build()).reserve("NETWORKING", ticketId);

        assertThat(reservation).isPresent();
        assertThat(reservation.get().id()).isEqualTo(technicianId);
        server.verify();
    }
}
