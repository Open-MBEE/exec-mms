package org.openmbee.spec.uml.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.hibernate.ObjectNotFoundException;
import org.openmbee.spec.uml.MofObject;

public class MofObjectSerializer extends StdSerializer<MofObject> {

    public MofObjectSerializer() {
        this(null);
    }

    public MofObjectSerializer(Class<MofObject> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(MofObject value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        try {
            if (value == null || value.getId() == null) {
                gen.writeNull();
                return;
            }
        } catch (ObjectNotFoundException e) {
            gen.writeNull();
            return;
        }
        gen.writeStartObject();
        gen.writeObjectField("@id", value.getId());
        gen.writeEndObject();
    }
}
