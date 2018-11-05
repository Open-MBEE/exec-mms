package org.openmbee.spec.uml;

public interface SendObjectAction extends InvocationAction, MofObject {
    // List<InputPin> getRequest();

    InputPin getTarget();
}
