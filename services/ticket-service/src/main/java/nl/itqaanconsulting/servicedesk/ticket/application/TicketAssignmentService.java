package nl.itqaanconsulting.servicedesk.ticket.application;

import nl.itqaanconsulting.servicedesk.ticket.domain.AssignmentStatus;
import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;
import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianClient;
import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianReservation;
import nl.itqaanconsulting.servicedesk.ticket.integration.NotificationClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TicketAssignmentService {

    private final TicketService ticketService;
    private final TechnicianClient technicianClient;
    private final NotificationClient notificationClient;

    public TicketAssignmentService(
            TicketService ticketService,
            TechnicianClient technicianClient,
            NotificationClient notificationClient
    ) {
        this.ticketService = ticketService;
        this.technicianClient = technicianClient;
        this.notificationClient = notificationClient;
    }

    public Ticket assign(UUID ticketId) {
        Ticket ticket = ticketService.get(ticketId);
        if (ticket.getAssignmentStatus() == AssignmentStatus.ASSIGNED) {
            return ticket;
        }

        Optional<TechnicianReservation> reservation = technicianClient.reserve(ticket.getRequiredSkill());
        if (reservation.isEmpty()) {
            return ticket;
        }

        Ticket assignedTicket = ticketService.assign(ticketId, reservation.get());
        notificationClient.sendAssignment(assignedTicket);
        return assignedTicket;
    }
}
