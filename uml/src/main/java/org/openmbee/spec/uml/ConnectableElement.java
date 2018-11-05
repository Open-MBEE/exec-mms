package org.openmbee.spec.uml;

import java.util.Collection;

public interface ConnectableElement extends TypedElement, ParameterableElement, MofObject {

    Collection<ConnectorEnd> getEnd();

    ConnectableElementTemplateParameter getTemplateParameter();
}
