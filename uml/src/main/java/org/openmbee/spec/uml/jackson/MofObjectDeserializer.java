package org.openmbee.spec.uml.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import org.hibernate.Session;
import org.openmbee.spec.uml.impl.MofObjectImpl;

public class MofObjectDeserializer extends StdDeserializer<MofObjectImpl> {

    private Session session;

    public MofObjectDeserializer(Session session) {
        super((Class<?>) null);
        this.session = session;
    }

    public MofObjectDeserializer() {
        this(null);
    }

    @Override
    public MofObjectImpl deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() != JsonToken.START_OBJECT) {
            throw new JsonParseException(p,
                "Expected START_OBJECT. Received " + p.currentName() + ".");
        }
        JsonToken token;
        MofObjectImpl mof = null;
        while ((token = p.nextToken()) != null && token != JsonToken.END_OBJECT) {
            if (mof == null && token == JsonToken.FIELD_NAME && "@id".equals(p.getCurrentName())) {
                p.nextToken();
                String id = p.getText();
                mof = session.get(MofObjectImpl.class, id);
                if (mof == null) {
                    throw new IOException("Unable to find an object with id " + id);
                }
            }
        }
        return mof;
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
        TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }
}
