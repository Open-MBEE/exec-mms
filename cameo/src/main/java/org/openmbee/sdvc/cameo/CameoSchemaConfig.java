package org.openmbee.sdvc.cameo;

import org.openmbee.sdvc.core.config.ProjectSchemas;
import org.openmbee.sdvc.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CameoSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("cameo").setDescription("cameo/md json handling, creates holding bins"));
    }
}
