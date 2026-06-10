package nl.itqaanconsulting.servicedesk.technician.api;

import jakarta.servlet.http.HttpServletRequest;
import nl.itqaanconsulting.servicedesk.technician.application.DuplicateTechnicianException;
import nl.itqaanconsulting.servicedesk.technician.application.NoAvailableTechnicianException;
import nl.itqaanconsulting.servicedesk.technician.application.TechnicianNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(TechnicianNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(
            TechnicianNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(DuplicateTechnicianException.class)
    ResponseEntity<ApiError> handleConflict(
            DuplicateTechnicianException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(NoAvailableTechnicianException.class)
    ResponseEntity<ApiError> handleNoAvailableTechnician(
            NoAvailableTechnicianException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return error(HttpStatus.BAD_REQUEST, "Request validation failed", request.getRequestURI(), fieldErrors);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}
