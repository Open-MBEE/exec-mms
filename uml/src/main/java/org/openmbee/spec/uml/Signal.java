package org.openmbee.spec.uml;

import java.util.List;

public interface Signal extends Classifier, MofObject {

    List<Property> getOwnedAttribute();
}
