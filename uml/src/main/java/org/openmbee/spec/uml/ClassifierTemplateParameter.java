package org.openmbee.spec.uml;

import java.util.Collection;

public interface ClassifierTemplateParameter extends TemplateParameter, MofObject {

    Boolean isAllowSubstitutable();

    Collection<Classifier> getConstrainingClassifier();

    Classifier getParameteredElement();
}
