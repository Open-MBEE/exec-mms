package org.openmbee.spec.uml;

public interface MultiplicityElement extends Element, MofObject {

    Boolean isOrdered();

    Boolean isUnique();

    Integer getLower();

    ValueSpecification getLowerValue();

    Integer getUpper();

    ValueSpecification getUpperValue();
}
