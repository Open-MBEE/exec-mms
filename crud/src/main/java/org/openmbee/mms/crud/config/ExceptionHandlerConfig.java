package org.openmbee.mms.crud.config;

import java.util.Map;
import org.openmbee.mms.core.exceptions.MMSException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ExceptionHandlerConfig extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { MMSException.class })
    protected ResponseEntity<Object> handleMMSException(MMSException ex, WebRequest request) {
        if (ex.getMessageObject() instanceof String) {
            ex.setMessageObject(Map.of("message", ex.getMessageObject()));
        }
        return handleExceptionInternal(ex, ex.getMessageObject(), new HttpHeaders(), ex.getCode(),
            request);
    }
}
