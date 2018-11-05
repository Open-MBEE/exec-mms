package org.openmbee.spec.uml;

public interface Substitution extends Realization, MofObject {

    Classifier getContract();

    Classifier getSubstitutingClassifier();
}
