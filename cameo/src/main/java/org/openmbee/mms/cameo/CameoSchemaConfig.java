package org.openmbee.mms.cameo;

import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CameoSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("cameo").setDescription("cameo/md json handling, creates holding bins"));
    }
}
