package org.openmbee.spec.uml;

import java.util.Collection;

public interface Component extends Class, MofObject {

    Boolean isIndirectlyInstantiated();

    Collection<PackageableElement> getPackagedElement();

    Collection<Interface> getProvided();

    Collection<ComponentRealization> getRealization();

    Collection<Interface> getRequired();
}
