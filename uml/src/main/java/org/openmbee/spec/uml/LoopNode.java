package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface LoopNode extends StructuredActivityNode, MofObject {

    List<OutputPin> getBodyOutput();

    Collection<ExecutableNode> getBodyPart();

    OutputPin getDecider();

    Boolean isTestedFirst();

    List<OutputPin> getLoopVariable();

    // List<InputPin> getLoopVariableInput();

    // List<OutputPin> getResult();

    Collection<ExecutableNode> getSetupPart();

    Collection<ExecutableNode> getTest();
}
