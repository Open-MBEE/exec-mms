package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Action extends ExecutableNode, MofObject {

    Classifier getContext();

    List<InputPin> getInput();

    Boolean isLocallyReentrant();

    Collection<Constraint> getLocalPostcondition();

    Collection<Constraint> getLocalPrecondition();

    List<OutputPin> getOutput();
}
