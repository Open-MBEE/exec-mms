package org.openmbee.spec.uml;

import java.util.Collection;

public interface Relationship extends Element, MofObject {

    Collection<Element> getRelatedElement();
}
