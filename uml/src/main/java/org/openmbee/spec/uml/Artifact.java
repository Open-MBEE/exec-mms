package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Artifact extends Classifier, DeployedArtifact, MofObject {

    String getFileName();

    Collection<Manifestation> getManifestation();

    Collection<Artifact> getNestedArtifact();

    List<Property> getOwnedAttribute();

    List<Operation> getOwnedOperation();
}
