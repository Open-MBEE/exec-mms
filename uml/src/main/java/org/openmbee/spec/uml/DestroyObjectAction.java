package org.openmbee.spec.uml;

public interface DestroyObjectAction extends Action, MofObject {

    Boolean isDestroyLinks();

    Boolean isDestroyOwnedObjects();

    InputPin getTarget();
}
