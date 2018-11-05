package org.openmbee.spec.uml;

public interface JoinNode extends ControlNode, MofObject {

    Boolean isCombineDuplicate();

    ValueSpecification getJoinSpec();
}
