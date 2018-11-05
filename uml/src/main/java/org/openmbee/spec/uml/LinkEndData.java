package org.openmbee.spec.uml;

import java.util.Collection;

public interface LinkEndData extends Element, MofObject {

    Property getEnd();

    Collection<QualifierValue> getQualifier();

    InputPin getValue();
}
