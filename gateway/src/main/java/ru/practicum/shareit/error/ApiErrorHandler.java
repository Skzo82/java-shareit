package ru.practicum.shareit.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiErrorHandler {

    private ResponseEntity<Map<String, String>> body(HttpStatus status, String message) {
        Map<String, String> m = new HashMap<>();
        m.put("error", (message == null || message.isBlank()) ? "Validation failed" : message);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(m);
    }

    @ExceptionHandler(org.springframework.web.client.HttpStatusCodeException.class)
    public ResponseEntity<String> handleHttpStatusCode(org.springframework.web.client.HttpStatusCodeException ex) {
        HttpHeaders out = new HttpHeaders();
        HttpHeaders in = ex.getResponseHeaders();
        if (in != null) {
            out.putAll(in);
        }
        if (!out.containsKey(HttpHeaders.CONTENT_TYPE)) {
            out.setContentType(MediaType.APPLICATION_JSON);
        }
        return ResponseEntity.status(ex.getStatusCode())
                .headers(out)
                .body(ex.getResponseBodyAsString());
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingHeader(MissingRequestHeaderException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var fe = ex.getBindingResult().getFieldError();
        String msg = (fe != null && fe.getDefaultMessage() != null) ? fe.getDefaultMessage() : "Validation failed";
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBind(BindException ex) {
        var fe = ex.getFieldError();
        String msg = (fe != null && fe.getDefaultMessage() != null) ? fe.getDefaultMessage() : "Validation failed";
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getMessage())
                .orElse("Validation failed");
        return body(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
