package org.openmbee.mms.crud.config;

import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("default").setDescription("default arbitrary json handling"));
    }
}
