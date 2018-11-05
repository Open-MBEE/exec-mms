package org.openmbee.spec.uml;

import java.util.Collection;

public interface Parameter extends MultiplicityElement, ConnectableElement, MofObject {

    String getDefault();

    ValueSpecification getDefaultValue();

    ParameterDirectionKind getDirection();

    ParameterEffectKind getEffect();

    Boolean isException();

    Boolean isStream();

    Operation getOperation();

    Collection<ParameterSet> getParameterSet();
}
