package nl.itqaanconsulting.servicedesk.notification.application;

import nl.itqaanconsulting.servicedesk.notification.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class NotificationStore {

    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public Notification save(Notification notification) {
        notifications.add(notification);
        return notification;
    }

    public List<Notification> findAll() {
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::createdAt).reversed())
                .toList();
    }
}
