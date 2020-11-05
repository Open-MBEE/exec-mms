package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openmbee.mms.json.SchemaJson;

public class SchemasResponse {

    private List<SchemaJson> schemas;

    public SchemasResponse() {
        schemas = new ArrayList<>();
    }

    public List<SchemaJson> getSchemas() {
        return schemas;
    }

    public SchemasResponse setSchemas(List<SchemaJson> schemas) {
        this.schemas = schemas;
        return this;
    }

    public SchemasResponse addAll(Collection<SchemaJson> all) {
        schemas.addAll(all);
        return this;
    }
}
