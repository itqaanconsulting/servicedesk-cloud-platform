package nl.itqaanconsulting.servicedesk.technician.api;

import jakarta.validation.Valid;
import nl.itqaanconsulting.servicedesk.technician.application.TechnicianService;
import nl.itqaanconsulting.servicedesk.technician.domain.Availability;
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
@RequestMapping("/api/technicians")
class TechnicianController {

    private final TechnicianService technicianService;

    TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping
    ResponseEntity<TechnicianResponse> create(@Valid @RequestBody CreateTechnicianRequest request) {
        TechnicianResponse response = TechnicianResponse.from(technicianService.create(
                request.name(),
                request.email(),
                request.skills()
        ));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{technicianId}")
    TechnicianResponse get(@PathVariable UUID technicianId) {
        return TechnicianResponse.from(technicianService.get(technicianId));
    }

    @GetMapping
    List<TechnicianResponse> find(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Availability availability
    ) {
        return technicianService.find(skill, availability).stream()
                .map(TechnicianResponse::from)
                .toList();
    }

    @PatchMapping("/{technicianId}/availability")
    TechnicianResponse changeAvailability(
            @PathVariable UUID technicianId,
            @Valid @RequestBody ChangeAvailabilityRequest request
    ) {
        return TechnicianResponse.from(
                technicianService.changeAvailability(technicianId, request.availability())
        );
    }
}
