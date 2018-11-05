package org.openmbee.spec.uml;

import java.util.Collection;

public interface InformationItem extends Classifier, MofObject {

    Collection<Classifier> getRepresented();
}
