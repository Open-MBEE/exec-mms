package org.openmbee.spec.uml;

import java.util.Collection;

public interface ComponentRealization extends Realization, MofObject {

    Component getAbstraction();

    Collection<Classifier> getRealizingClassifier();
}
