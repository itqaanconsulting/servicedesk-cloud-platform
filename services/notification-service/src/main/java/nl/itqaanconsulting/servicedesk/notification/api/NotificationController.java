package nl.itqaanconsulting.servicedesk.notification.api;

import jakarta.validation.Valid;
import nl.itqaanconsulting.servicedesk.notification.application.NotificationService;
import nl.itqaanconsulting.servicedesk.notification.domain.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
class NotificationController {

    private final NotificationService notificationService;

    NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Notification create(@Valid @RequestBody CreateNotificationRequest request) {
        return notificationService.deliver(request);
    }

    @GetMapping
    List<Notification> findAll() {
        return notificationService.findAll();
    }
}
