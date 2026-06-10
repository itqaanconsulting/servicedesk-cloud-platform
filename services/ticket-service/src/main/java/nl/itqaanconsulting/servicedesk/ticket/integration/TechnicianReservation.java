package nl.itqaanconsulting.servicedesk.ticket.integration;

import java.util.UUID;

public record TechnicianReservation(
        UUID id,
        String name,
        String email
) {
}
