package nl.itqaanconsulting.servicedesk.technician.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
class ServiceInfoController {

    @GetMapping
    Map<String, String> serviceInfo() {
        return Map.of(
                "service", "technician-service",
                "responsibility", "Technician skills and availability"
        );
    }
}
