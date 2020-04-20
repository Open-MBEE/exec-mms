package org.openmbee.sdvc.jupyter;

import org.openmbee.sdvc.core.config.ProjectSchemas;
import org.openmbee.sdvc.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JupyterSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("jupyter").setDescription("jupyter json handling"));
    }
}
