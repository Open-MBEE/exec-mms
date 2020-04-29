package org.openmbee.sdvc.core.config;

import java.util.HashMap;
import java.util.Map;
import org.openmbee.sdvc.json.SchemaJson;
import org.springframework.stereotype.Component;

@Component
public class ProjectSchemas {

    private Map<String, SchemaJson> schemas = new HashMap<>();

    public void addSchema(SchemaJson schema) {
        schemas.put(schema.getName(), schema);
    }

    public Map<String, SchemaJson> getSchemas() {
        return schemas;
    }
}
