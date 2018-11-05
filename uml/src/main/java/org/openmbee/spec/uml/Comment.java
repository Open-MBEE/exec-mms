package org.openmbee.spec.uml;

import java.util.Collection;

public interface Comment extends Element, MofObject {

    Collection<Element> getAnnotatedElement();

    String getBody();
}
