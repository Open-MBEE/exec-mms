package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface StructuredClassifier extends Classifier, MofObject {

    List<Property> getOwnedAttribute();

    Collection<Connector> getOwnedConnector();

    Collection<Property> getPart();

    Collection<ConnectableElement> getRole();
}
