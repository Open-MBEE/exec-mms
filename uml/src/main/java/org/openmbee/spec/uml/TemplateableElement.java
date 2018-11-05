package org.openmbee.spec.uml;

import java.util.Collection;

public interface TemplateableElement extends Element, MofObject {

    TemplateSignature getOwnedTemplateSignature();

    Collection<TemplateBinding> getTemplateBinding();
}
