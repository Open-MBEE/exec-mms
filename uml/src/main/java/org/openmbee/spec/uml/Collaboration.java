package org.openmbee.spec.uml;

import java.util.Collection;

public interface Collaboration extends StructuredClassifier, BehavioredClassifier, MofObject {

    Collection<ConnectableElement> getCollaborationRole();
}
