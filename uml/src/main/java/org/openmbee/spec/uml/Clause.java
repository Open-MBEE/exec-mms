package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Clause extends Element, MofObject {

    Collection<ExecutableNode> getBody();

    List<OutputPin> getBodyOutput();

    OutputPin getDecider();

    Collection<Clause> getPredecessorClause();

    Collection<Clause> getSuccessorClause();

    Collection<ExecutableNode> getTest();
}
