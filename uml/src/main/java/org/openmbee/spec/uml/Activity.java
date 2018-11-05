package org.openmbee.spec.uml;

import java.util.Collection;

public interface Activity extends Behavior, MofObject {

    Collection<ActivityEdge> getEdge();

    Collection<ActivityGroup> getGroup();

    Boolean isReadOnly();

    Boolean isSingleExecution();

    Collection<ActivityNode> getNode();

    Collection<ActivityPartition> getPartition();

    Collection<StructuredActivityNode> getStructuredNode();

    Collection<Variable> getVariable();
}
