package org.openmbee.spec.uml;

public interface DeploymentSpecification extends Artifact, MofObject {

    Deployment getDeployment();

    String getDeploymentLocation();

    String getExecutionLocation();
}
