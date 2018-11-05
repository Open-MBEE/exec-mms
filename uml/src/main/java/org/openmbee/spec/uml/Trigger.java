package org.openmbee.spec.uml;

import java.util.Collection;

public interface Trigger extends NamedElement, MofObject {

    Event getEvent();

    Collection<Port> getPort();
}
