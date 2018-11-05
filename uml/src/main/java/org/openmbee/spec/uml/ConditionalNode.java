package org.openmbee.spec.uml;

import java.util.Collection;

public interface ConditionalNode extends StructuredActivityNode, MofObject {

    Collection<Clause> getClause();

    Boolean isAssured();

    Boolean isDeterminate();

    // List<OutputPin> getResult();
}
