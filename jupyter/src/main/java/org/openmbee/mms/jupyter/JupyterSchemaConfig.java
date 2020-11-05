package org.openmbee.mms.jupyter;

import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.json.SchemaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JupyterSchemaConfig {

    @Autowired
    public void registerSchema(ProjectSchemas schemas) {
        schemas.addSchema(new SchemaJson().setName("jupyter").setDescription("jupyter json handling"));
    }
}
