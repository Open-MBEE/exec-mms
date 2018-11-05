package org.openmbee.spec.uml;

import java.util.List;

public interface OpaqueBehavior extends Behavior, MofObject {

    List<String> getBody();

    List<String> getLanguage();
}
