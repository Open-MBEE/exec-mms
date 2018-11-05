package org.openmbee.spec.uml;

import java.util.Collection;

public interface ActivityPartition extends ActivityGroup, MofObject {

    Collection<ActivityEdge> getEdge();

    Boolean isDimension();

    Boolean isExternal();

    Collection<ActivityNode> getNode();

    Element getRepresents();

    Collection<ActivityPartition> getSubpartition();

    ActivityPartition getSuperPartition();
}
