package nl.itqaanconsulting.servicedesk.notification.application;

import io.micrometer.core.instrument.MeterRegistry;
import nl.itqaanconsulting.servicedesk.notification.api.CreateNotificationRequest;
import nl.itqaanconsulting.servicedesk.notification.domain.Notification;
import nl.itqaanconsulting.servicedesk.notification.domain.NotificationStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationStore store;
    private final MeterRegistry meterRegistry;

    public NotificationService(NotificationStore store, MeterRegistry meterRegistry) {
        this.store = store;
        this.meterRegistry = meterRegistry;
    }

    public Notification deliver(CreateNotificationRequest request) {
        boolean simulatedFailure = request.recipient().endsWith("@delivery-failure.test");
        NotificationStatus status = simulatedFailure ? NotificationStatus.FAILED : NotificationStatus.DELIVERED;
        Notification notification = store.save(new Notification(
                UUID.randomUUID(),
                request.ticketId(),
                request.type(),
                request.recipient(),
                request.subject(),
                request.message(),
                status,
                simulatedFailure ? "Simulated email provider rejection" : null,
                Instant.now()
        ));
        meterRegistry.counter("servicedesk.notifications", "status", status.name()).increment();
        return notification;
    }

    public List<Notification> findAll() {
        return store.findAll();
    }
}
