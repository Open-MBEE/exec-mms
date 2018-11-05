package org.openmbee.spec.uml;

import java.util.Collection;

public interface RedefinableElement extends NamedElement, MofObject {

    Boolean isLeaf();

    Collection<RedefinableElement> getRedefinedElement();

    Collection<Classifier> getRedefinitionContext();
}
