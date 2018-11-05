package org.openmbee.spec.uml;

import java.util.Collection;

public interface TemplateBinding extends DirectedRelationship, MofObject {

    TemplateableElement getBoundElement();

    Collection<TemplateParameterSubstitution> getParameterSubstitution();

    TemplateSignature getSignature();
}
