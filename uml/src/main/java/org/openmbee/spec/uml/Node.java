package org.openmbee.spec.uml;

import java.util.Collection;

public interface Node extends Class, DeploymentTarget, MofObject {

    Collection<Node> getNestedNode();
}
