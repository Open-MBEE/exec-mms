package org.openmbee.spec.uml;

import java.util.Collection;

public interface UseCase extends BehavioredClassifier, MofObject {

    Collection<Extend> getExtend();

    Collection<ExtensionPoint> getExtensionPoint();

    Collection<Include> getInclude();

    Collection<Classifier> getSubject();
}
