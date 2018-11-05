package org.openmbee.spec.uml;

import java.util.List;

public interface DataType extends Classifier, MofObject {

    List<Property> getOwnedAttribute();

    List<Operation> getOwnedOperation();
}
