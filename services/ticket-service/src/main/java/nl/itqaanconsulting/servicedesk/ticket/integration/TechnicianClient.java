package nl.itqaanconsulting.servicedesk.ticket.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

@Component
public class TechnicianClient {

    static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final RestClient restClient;

    public TechnicianClient(@Qualifier("technicianRestClient") RestClient technicianRestClient) {
        this.restClient = technicianRestClient;
    }

    @Retry(name = "technicianService")
    @CircuitBreaker(name = "technicianService", fallbackMethod = "fallback")
    public Optional<TechnicianReservation> reserve(String skill, UUID ticketId) {
        try {
            // The ticket id is the idempotency key: a retry after a timeout returns the same
            // technician instead of reserving a second one.
            TechnicianReservation reservation = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/technicians/reservations")
                            .queryParam("skill", skill)
                            .build())
                    .header(IDEMPOTENCY_KEY_HEADER, ticketId.toString())
                    .retrieve()
                    .body(TechnicianReservation.class);
            return Optional.ofNullable(reservation);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw exception;
        }
    }

    public Optional<TechnicianReservation> fallback(String skill, UUID ticketId, Throwable exception) {
        return Optional.empty();
    }
}
