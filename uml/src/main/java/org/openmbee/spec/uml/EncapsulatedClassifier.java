package org.openmbee.spec.uml;

import java.util.Collection;

public interface EncapsulatedClassifier extends StructuredClassifier, MofObject {

    Collection<Port> getOwnedPort();
}
