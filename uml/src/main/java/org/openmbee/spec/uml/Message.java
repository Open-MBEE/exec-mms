package org.openmbee.spec.uml;

import java.util.List;

public interface Message extends NamedElement, MofObject {

    List<ValueSpecification> getArgument();

    Connector getConnector();

    Interaction getInteraction();

    MessageKind getMessageKind();

    MessageSort getMessageSort();

    MessageEnd getReceiveEvent();

    MessageEnd getSendEvent();

    NamedElement getSignature();
}
