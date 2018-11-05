package org.openmbee.spec.uml;

public interface Feature extends RedefinableElement, MofObject {

    Classifier getFeaturingClassifier();

    Boolean isStatic();
}
