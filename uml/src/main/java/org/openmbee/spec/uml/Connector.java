package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Connector extends Feature, MofObject {

    Collection<Behavior> getContract();

    List<ConnectorEnd> getEnd();

    ConnectorKind getKind();

    Collection<Connector> getRedefinedConnector();

    Association getType();
}
