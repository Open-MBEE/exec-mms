package org.openmbee.spec.uml;

import java.util.Collection;

public interface ActivityGroup extends NamedElement, MofObject {

    Collection<ActivityEdge> getContainedEdge();

    Collection<ActivityNode> getContainedNode();

    Activity getInActivity();

    Collection<ActivityGroup> getSubgroup();

    ActivityGroup getSuperGroup();
}
