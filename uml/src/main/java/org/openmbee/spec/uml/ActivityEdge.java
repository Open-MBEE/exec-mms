package org.openmbee.spec.uml;

import java.util.Collection;

public interface ActivityEdge extends RedefinableElement, MofObject {

    Activity getActivity();

    ValueSpecification getGuard();

    Collection<ActivityGroup> getInGroup();

    Collection<ActivityPartition> getInPartition();

    StructuredActivityNode getInStructuredNode();

    InterruptibleActivityRegion getInterrupts();

    Collection<ActivityEdge> getRedefinedEdge();

    ActivityNode getSource();

    ActivityNode getTarget();

    ValueSpecification getWeight();
}
