package org.openmbee.spec.uml;

public interface TemplateParameterSubstitution extends Element, MofObject {

    ParameterableElement getActual();

    TemplateParameter getFormal();

    ParameterableElement getOwnedActual();

    TemplateBinding getTemplateBinding();
}
