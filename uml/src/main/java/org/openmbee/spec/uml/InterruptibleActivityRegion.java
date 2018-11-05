package org.openmbee.spec.uml;

import java.util.Collection;

public interface InterruptibleActivityRegion extends ActivityGroup, MofObject {

    Collection<ActivityEdge> getInterruptingEdge();

    Collection<ActivityNode> getNode();
}
