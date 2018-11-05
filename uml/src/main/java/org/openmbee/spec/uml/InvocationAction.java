package org.openmbee.spec.uml;

import java.util.List;

public interface InvocationAction extends Action, MofObject {

    List<InputPin> getArgument();

    Port getOnPort();
}
