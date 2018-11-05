package org.openmbee.spec.uml;

import java.util.Collection;

public interface RedefinableTemplateSignature extends RedefinableElement, TemplateSignature,
    MofObject {

    Classifier getClassifier();

    Collection<RedefinableTemplateSignature> getExtendedSignature();

    Collection<TemplateParameter> getInheritedParameter();
}
