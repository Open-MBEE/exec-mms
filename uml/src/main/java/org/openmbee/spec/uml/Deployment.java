package org.openmbee.spec.uml;

import java.util.Collection;

public interface Deployment extends Dependency, MofObject {

    Collection<DeploymentSpecification> getConfiguration();

    Collection<DeployedArtifact> getDeployedArtifact();

    DeploymentTarget getLocation();
}
