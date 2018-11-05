package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Property extends ConnectableElement, DeploymentTarget, StructuralFeature,
    MofObject {

    AggregationKind getAggregation();

    Association getAssociation();

    Property getAssociationEnd();

    Class getClass_();

    DataType getDatatype();

    ValueSpecification getDefaultValue();

    Interface getInterface();

    Boolean isComposite();

    Boolean isDerived();

    Boolean isDerivedUnion();

    Boolean isID();

    Property getOpposite();

    Association getOwningAssociation();

    List<Property> getQualifier();

    Collection<Property> getRedefinedProperty();

    Collection<Property> getSubsettedProperty();
}
