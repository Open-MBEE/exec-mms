package org.openmbee.spec.uml;

public interface DecisionNode extends ControlNode, MofObject {

    Behavior getDecisionInput();

    ObjectFlow getDecisionInputFlow();
}
