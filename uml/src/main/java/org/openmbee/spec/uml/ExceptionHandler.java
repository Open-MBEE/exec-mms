package org.openmbee.spec.uml;

import java.util.Collection;

public interface ExceptionHandler extends Element, MofObject {

    ObjectNode getExceptionInput();

    Collection<Classifier> getExceptionType();

    ExecutableNode getHandlerBody();

    ExecutableNode getProtectedNode();
}
