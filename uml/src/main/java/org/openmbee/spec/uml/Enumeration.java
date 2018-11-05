package org.openmbee.spec.uml;

import java.util.List;

public interface Enumeration extends DataType, MofObject {

    List<EnumerationLiteral> getOwnedLiteral();
}
