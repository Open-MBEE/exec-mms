package org.openmbee.spec.uml;

import java.util.Collection;

public interface BehavioredClassifier extends Classifier, MofObject {

    Behavior getClassifierBehavior();

    Collection<InterfaceRealization> getInterfaceRealization();

    Collection<Behavior> getOwnedBehavior();
}
