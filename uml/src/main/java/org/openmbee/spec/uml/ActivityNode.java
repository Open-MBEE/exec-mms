package org.openmbee.spec.uml;

import java.util.Collection;

public interface ActivityNode extends RedefinableElement, MofObject {

    Activity getActivity();

    Collection<ActivityGroup> getInGroup();

    Collection<InterruptibleActivityRegion> getInInterruptibleRegion();

    Collection<ActivityPartition> getInPartition();

    StructuredActivityNode getInStructuredNode();

    Collection<ActivityEdge> getIncoming();

    Collection<ActivityEdge> getOutgoing();

    Collection<ActivityNode> getRedefinedNode();
}
