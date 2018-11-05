package org.openmbee.spec.uml;

import java.util.Collection;

public interface InstanceSpecification extends DeploymentTarget, PackageableElement,
    DeployedArtifact, MofObject {

    Collection<Classifier> getClassifier();

    Collection<Slot> getSlot();

    ValueSpecification getSpecification();
}
