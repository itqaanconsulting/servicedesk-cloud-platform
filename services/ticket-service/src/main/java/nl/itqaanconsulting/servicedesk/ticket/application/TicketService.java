package nl.itqaanconsulting.servicedesk.ticket.application;

import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketPriority;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketRepository;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket create(String title, String description, String requesterEmail, TicketPriority priority) {
        return ticketRepository.save(new Ticket(title, description, requesterEmail, priority));
    }

    @Transactional(readOnly = true)
    public Ticket get(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    @Transactional(readOnly = true)
    public List<Ticket> find(TicketStatus status) {
        if (status == null) {
            return ticketRepository.findAllByOrderByCreatedAtDesc();
        }
        return ticketRepository.findAllByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional
    public Ticket changeStatus(UUID ticketId, TicketStatus status) {
        Ticket ticket = get(ticketId);
        ticket.changeStatus(status);
        return ticket;
    }
}
