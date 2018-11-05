package org.openmbee.spec.uml;

public interface ReadIsClassifiedObjectAction extends Action, MofObject {

    Classifier getClassifier();

    Boolean isDirect();

    InputPin getObject();

    OutputPin getResult();
}
