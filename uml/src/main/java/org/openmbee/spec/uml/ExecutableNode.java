package org.openmbee.spec.uml;

import java.util.Collection;

public interface ExecutableNode extends ActivityNode, MofObject {

    Collection<ExceptionHandler> getHandler();
}
