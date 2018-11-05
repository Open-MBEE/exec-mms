package org.openmbee.spec.uml;

public interface CreateObjectAction extends Action, MofObject {

    Classifier getClassifier();

    OutputPin getResult();
}
