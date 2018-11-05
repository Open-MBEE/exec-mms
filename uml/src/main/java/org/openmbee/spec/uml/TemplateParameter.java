package org.openmbee.spec.uml;

public interface TemplateParameter extends Element, MofObject {

    ParameterableElement getDefault();

    ParameterableElement getOwnedDefault();

    ParameterableElement getOwnedParameteredElement();

    ParameterableElement getParameteredElement();

    TemplateSignature getSignature();
}
