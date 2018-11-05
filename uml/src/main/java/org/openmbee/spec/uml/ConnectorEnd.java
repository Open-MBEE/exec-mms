package org.openmbee.spec.uml;

public interface ConnectorEnd extends MultiplicityElement, MofObject {

    Property getDefiningEnd();

    Property getPartWithPort();

    ConnectableElement getRole();
}
