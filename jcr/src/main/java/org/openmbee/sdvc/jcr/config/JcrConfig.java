package org.openmbee.sdvc.jcr.config;

import javax.jcr.Repository;
import org.apache.jackrabbit.oak.Oak;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public abstract class JcrConfig {
    //String uri = "mongodb://" + host + ":" + port;
    //Node ns = new DocumentMK.Builder().setMongoDB(uri, "oak_demo", 16).getNodeStore();
    //Repository repo = new Jcr(new Oak(ns)).createRepository();
}
