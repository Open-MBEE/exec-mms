package org.openmbee.spec.uml;

public interface InterfaceRealization extends Realization, MofObject {

    Interface getContract();

    BehavioredClassifier getImplementingClassifier();
}
