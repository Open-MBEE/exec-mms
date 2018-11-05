package org.openmbee.spec.uml;

import java.util.List;

public interface TemplateSignature extends Element, MofObject {

    List<TemplateParameter> getOwnedParameter();

    List<TemplateParameter> getParameter();

    TemplateableElement getTemplate();
}
