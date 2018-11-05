package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface AcceptEventAction extends Action, MofObject {

    Boolean isUnmarshall();

    List<OutputPin> getResult();

    Collection<Trigger> getTrigger();
}
