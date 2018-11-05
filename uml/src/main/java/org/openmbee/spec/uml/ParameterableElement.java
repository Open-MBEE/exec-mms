package org.openmbee.spec.uml;

public interface ParameterableElement extends Element, MofObject {

    TemplateParameter getOwningTemplateParameter();

    TemplateParameter getTemplateParameter();
}
