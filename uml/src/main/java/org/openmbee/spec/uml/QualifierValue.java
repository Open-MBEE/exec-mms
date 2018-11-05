package org.openmbee.spec.uml;

public interface QualifierValue extends Element, MofObject {

    Property getQualifier();

    InputPin getValue();
}
