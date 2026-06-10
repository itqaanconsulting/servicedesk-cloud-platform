package nl.itqaanconsulting.servicedesk.ticket.api;

import jakarta.validation.Valid;
import nl.itqaanconsulting.servicedesk.ticket.application.TicketService;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
class TicketController {

    private final TicketService ticketService;

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = TicketResponse.from(ticketService.create(
                request.title(),
                request.description(),
                request.requesterEmail(),
                request.priority()
        ));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{ticketId}")
    TicketResponse get(@PathVariable UUID ticketId) {
        return TicketResponse.from(ticketService.get(ticketId));
    }

    @GetMapping
    List<TicketResponse> find(@RequestParam(required = false) TicketStatus status) {
        return ticketService.find(status).stream()
                .map(TicketResponse::from)
                .toList();
    }

    @PatchMapping("/{ticketId}/status")
    TicketResponse changeStatus(
            @PathVariable UUID ticketId,
            @Valid @RequestBody ChangeTicketStatusRequest request
    ) {
        return TicketResponse.from(ticketService.changeStatus(ticketId, request.status()));
    }
}
