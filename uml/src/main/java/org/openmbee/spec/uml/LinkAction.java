package org.openmbee.spec.uml;

import java.util.Collection;

public interface LinkAction extends Action, MofObject {

    Collection<LinkEndData> getEndData();

    Collection<InputPin> getInputValue();
}
