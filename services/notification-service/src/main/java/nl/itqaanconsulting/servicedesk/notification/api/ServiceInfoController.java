package nl.itqaanconsulting.servicedesk.notification.api;

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
                "service", "notification-service",
                "responsibility", "Notification delivery and audit history"
        );
    }
}
