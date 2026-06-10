package nl.itqaanconsulting.servicedesk.ticket.application;

import nl.itqaanconsulting.servicedesk.ticket.domain.AssignmentStatus;
import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;
import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianClient;
import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianReservation;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TicketAssignmentService {

    private final TicketService ticketService;
    private final TechnicianClient technicianClient;

    public TicketAssignmentService(TicketService ticketService, TechnicianClient technicianClient) {
        this.ticketService = ticketService;
        this.technicianClient = technicianClient;
    }

    public Ticket assign(UUID ticketId) {
        Ticket ticket = ticketService.get(ticketId);
        if (ticket.getAssignmentStatus() == AssignmentStatus.ASSIGNED) {
            return ticket;
        }

        Optional<TechnicianReservation> reservation = technicianClient.reserve(ticket.getRequiredSkill());
        return reservation
                .map(technician -> ticketService.assign(ticketId, technician))
                .orElse(ticket);
    }
}
