package org.openmbee.spec.uml;

import java.util.List;

public interface UnmarshallAction extends Action, MofObject {

    InputPin getObject();

    List<OutputPin> getResult();

    Classifier getUnmarshallType();
}
