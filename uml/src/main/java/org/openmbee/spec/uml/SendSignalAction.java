package org.openmbee.spec.uml;

public interface SendSignalAction extends InvocationAction, MofObject {

    Signal getSignal();

    InputPin getTarget();
}
