package nl.itqaanconsulting.servicedesk.ticket.integration;

import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;

import java.util.UUID;

record AssignmentNotificationRequest(
        UUID ticketId,
        String type,
        String recipient,
        String subject,
        String message
) {

    static AssignmentNotificationRequest from(Ticket ticket) {
        return new AssignmentNotificationRequest(
                ticket.getId(),
                "TICKET_ASSIGNED",
                ticket.getRequesterEmail(),
                "Ticket assigned: " + ticket.getTitle(),
                "Your ticket was assigned to " + ticket.getAssignedTechnicianName() + "."
        );
    }
}
