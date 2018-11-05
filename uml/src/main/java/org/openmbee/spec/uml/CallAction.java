package org.openmbee.spec.uml;

import java.util.List;

public interface CallAction extends InvocationAction, MofObject {

    Boolean isSynchronous();

    List<OutputPin> getResult();
}
