package org.openmbee.spec.uml;

import java.util.List;

public interface OpaqueExpression extends ValueSpecification, MofObject {

    Behavior getBehavior();

    List<String> getBody();

    List<String> getLanguage();

    Parameter getResult();
}
