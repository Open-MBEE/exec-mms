package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface BehavioralFeature extends Feature, Namespace, MofObject {

    CallConcurrencyKind getConcurrency();

    Boolean isAbstract();

    Collection<Behavior> getMethod();

    List<Parameter> getOwnedParameter();

    Collection<ParameterSet> getOwnedParameterSet();

    Collection<Type> getRaisedException();
}
