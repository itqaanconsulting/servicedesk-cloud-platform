package nl.itqaanconsulting.servicedesk.ticket.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findAllByStatusOrderByCreatedAtDesc(TicketStatus status);

    List<Ticket> findAllByOrderByCreatedAtDesc();
}
