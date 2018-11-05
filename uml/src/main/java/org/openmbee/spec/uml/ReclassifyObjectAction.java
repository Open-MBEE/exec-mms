package org.openmbee.spec.uml;

import java.util.Collection;

public interface ReclassifyObjectAction extends Action, MofObject {

    Boolean isReplaceAll();

    Collection<Classifier> getNewClassifier();

    InputPin getObject();

    Collection<Classifier> getOldClassifier();
}
