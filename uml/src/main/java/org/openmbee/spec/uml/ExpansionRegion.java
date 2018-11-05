package org.openmbee.spec.uml;

import java.util.Collection;

public interface ExpansionRegion extends StructuredActivityNode, MofObject {

    Collection<ExpansionNode> getInputElement();

    ExpansionKind getMode();

    Collection<ExpansionNode> getOutputElement();
}
