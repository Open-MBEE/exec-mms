package org.openmbee.spec.uml;

import java.util.Collection;

public interface Generalization extends DirectedRelationship, MofObject {

    Classifier getGeneral();

    Collection<GeneralizationSet> getGeneralizationSet();

    Boolean isSubstitutable();

    Classifier getSpecific();
}
