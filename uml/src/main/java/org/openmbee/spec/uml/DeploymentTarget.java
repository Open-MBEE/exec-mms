package org.openmbee.spec.uml;

import java.util.Collection;

public interface DeploymentTarget extends NamedElement, MofObject {

    Collection<PackageableElement> getDeployedElement();

    Collection<Deployment> getDeployment();
}
