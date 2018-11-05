package org.openmbee.spec.uml;

public interface ReadExtentAction extends Action, MofObject {

    Classifier getClassifier();

    OutputPin getResult();
}
