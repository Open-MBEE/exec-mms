package org.openmbee.mms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;

@JsonIgnoreProperties({"empty"})
@Schema(name = "Schema")
public class SchemaJson extends HashMap<String, Object> {

    public String getName() {
        return (String) this.get(BaseJson.NAME);
    }

    public SchemaJson setName(String name) {
        this.put(BaseJson.NAME, name);
        return this;
    }

    public String getDescription() {
        return (String) this.get("description");
    }

    public SchemaJson setDescription(String name) {
        this.put("description", name);
        return this;
    }
}
