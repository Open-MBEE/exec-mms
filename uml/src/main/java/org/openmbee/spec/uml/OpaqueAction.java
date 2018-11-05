package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface OpaqueAction extends Action, MofObject {

    List<String> getBody();

    Collection<InputPin> getInputValue();

    List<String> getLanguage();

    Collection<OutputPin> getOutputValue();
}
