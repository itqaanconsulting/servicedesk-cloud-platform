package nl.itqaanconsulting.servicedesk.ticket.api;

import java.time.Instant;
import java.util.Map;

record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
