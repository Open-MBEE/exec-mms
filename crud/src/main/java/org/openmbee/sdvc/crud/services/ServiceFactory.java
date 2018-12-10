package org.openmbee.sdvc.crud.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ServiceFactory implements ApplicationContextAware {

    private ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public NodeService getNodeService(String type) {
        try {
            NodeService ns = context.getBean(type + "NodeService", NodeService.class);
            if (ns != null) {
                return ns;
            }
        } catch (BeansException e) {
        }
        return context.getBean("defaultNodeService", NodeService.class);
    }

    public ProjectService getProjectService(String type) {
        try {
            ProjectService ps = context.getBean(type + "ProjectService", ProjectService.class);
            if (ps != null) {
                return ps;
            }
        } catch (BeansException e) {
        }
        return context.getBean("defaultProjectService", ProjectService.class);
    }

}
