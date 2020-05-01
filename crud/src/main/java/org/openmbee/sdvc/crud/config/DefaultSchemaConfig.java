package org.openmbee.sdvc.crud.config;

import org.openmbee.sdvc.core.config.ProjectSchemas;
import org.openmbee.sdvc.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("default").setDescription("default arbitrary json handling"));
    }
}
