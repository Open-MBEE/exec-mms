package org.openmbee.spec.uml.jackson;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import org.hibernate.Session;
import org.openmbee.spec.uml.impl.MofObjectImpl;

public class HibernateEntityIdResolver extends SimpleObjectIdResolver {

    private Session session;

    public HibernateEntityIdResolver(Session session) {
        this.session = session;
    }

    @Override
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        Object resolved = super.resolveId(id);
        if (resolved == null) {
            resolved = session.get(MofObjectImpl.class, (String) id.key);
            if (resolved != null) {
                super.bindItem(id, resolved);
            }
        }
        return resolved;
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return new HibernateEntityIdResolver(session);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType.getClass() == HibernateEntityIdResolver.class;
    }
}
