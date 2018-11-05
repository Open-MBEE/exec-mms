package org.openmbee.spec.uml;

import java.util.Collection;

public interface StructuredActivityNode extends Namespace, ActivityGroup, Action, MofObject {

    Activity getActivity();

    Collection<ActivityEdge> getEdge();

    Boolean isMustIsolate();

    Collection<ActivityNode> getNode();

    Collection<InputPin> getStructuredNodeInput();

    Collection<OutputPin> getStructuredNodeOutput();

    Collection<Variable> getVariable();
}
