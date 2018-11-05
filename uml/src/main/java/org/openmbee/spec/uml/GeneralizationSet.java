package org.openmbee.spec.uml;

import java.util.Collection;

public interface GeneralizationSet extends PackageableElement, MofObject {

    Collection<Generalization> getGeneralization();

    Boolean isCovering();

    Boolean isDisjoint();

    Classifier getPowertype();
}
