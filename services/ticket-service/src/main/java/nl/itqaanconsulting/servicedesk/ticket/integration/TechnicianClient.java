package nl.itqaanconsulting.servicedesk.ticket.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class TechnicianClient {

    private final RestClient restClient;

    public TechnicianClient(@Qualifier("technicianRestClient") RestClient technicianRestClient) {
        this.restClient = technicianRestClient;
    }

    @Retry(name = "technicianService")
    @CircuitBreaker(name = "technicianService", fallbackMethod = "fallback")
    public Optional<TechnicianReservation> reserve(String skill) {
        try {
            TechnicianReservation reservation = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/technicians/reservations")
                            .queryParam("skill", skill)
                            .build())
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

    public Optional<TechnicianReservation> fallback(String skill, Throwable exception) {
        return Optional.empty();
    }
}
