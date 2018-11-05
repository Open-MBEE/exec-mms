package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Behavior extends Class, MofObject {

    BehavioredClassifier getContext();

    Boolean isReentrant();

    List<Parameter> getOwnedParameter();

    Collection<ParameterSet> getOwnedParameterSet();

    Collection<Constraint> getPostcondition();

    Collection<Constraint> getPrecondition();

    BehavioralFeature getSpecification();

    Collection<Behavior> getRedefinedBehavior();
}
