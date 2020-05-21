package org.openmbee.sdvc.core.intercept;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public abstract class SdvcHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
    public abstract void register(InterceptorRegistry registry);
}
