package org.openmbee.sdvc.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class RequestUtils {

    private static Logger logger = LogManager.getLogger(RequestUtils.class);

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    public void setRequestMappingHandlerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public static Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
            .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
            .map(ServletRequestAttributes::getRequest);
    }

    public HandlerMethod getHandlerMethod() {
        Optional<HttpServletRequest> optionalRequest = getCurrentHttpRequest();
        if(optionalRequest.isPresent()) {
            try {
                HandlerExecutionChain handler = requestMappingHandlerMapping.getHandler(optionalRequest.get());
                Object handlerObject = handler.getHandler();
                if(handlerObject instanceof HandlerMethod) {
                    return (HandlerMethod) handlerObject;
                } else {
                    throw new InternalErrorException("Unrecognized Handler type: " + handler.getClass().getCanonicalName());
                }
            } catch (Exception e) {
                throw new InternalErrorException("Could not get originating controller for current request: " + e.getMessage());
            }
        } else {
            throw new InternalErrorException("getHandlerMethod was called with no current http request");
        }
    }
}
