package org.openmbee.sdvc.crud.services;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class NodeServiceFactory implements ApplicationContextAware {
    private ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public NodeService getNodeService(String type) {
        NodeService service = (NodeService)context.getBean(type + "NodeService");
        return service;
    }

}
