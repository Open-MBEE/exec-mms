package org.openmbee.sdvc.crud.config;

import org.openmbee.sdvc.core.exceptions.SdvcException;
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

    @ExceptionHandler(value = { SdvcException.class })
    protected ResponseEntity<Object> handleSdvcException(SdvcException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessageObject(), new HttpHeaders(), ex.getCode(),
            request);
    }
}
