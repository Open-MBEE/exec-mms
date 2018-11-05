package org.openmbee.spec.uml;

import java.util.Collection;

public interface Operation extends TemplateableElement, ParameterableElement, BehavioralFeature,
    MofObject {

    Constraint getBodyCondition();

    Class getClass_();

    DataType getDatatype();

    Interface getInterface();

    Boolean isOrdered();

    Boolean isQuery();

    Boolean isUnique();

    Integer getLower();

    // List<Parameter> getOwnedParameter();

    Collection<Constraint> getPostcondition();

    Collection<Constraint> getPrecondition();

    // Collection<Type> getRaisedException();

    Collection<Operation> getRedefinedOperation();

    OperationTemplateParameter getTemplateParameter();

    Type getType();

    Integer getUpper();
}
