package org.openmbee.spec.uml;

import java.util.List;

public interface Expression extends ValueSpecification, MofObject {

    List<ValueSpecification> getOperand();

    String getSymbol();
}
