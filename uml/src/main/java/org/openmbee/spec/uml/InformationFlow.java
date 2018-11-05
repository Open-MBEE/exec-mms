package org.openmbee.spec.uml;

import java.util.Collection;

public interface InformationFlow extends DirectedRelationship, PackageableElement, MofObject {

    Collection<Classifier> getConveyed();

    Collection<NamedElement> getInformationSource();

    Collection<NamedElement> getInformationTarget();

    Collection<Relationship> getRealization();

    Collection<ActivityEdge> getRealizingActivityEdge();

    Collection<Connector> getRealizingConnector();

    Collection<Message> getRealizingMessage();
}
