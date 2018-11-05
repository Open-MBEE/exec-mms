package org.openmbee.spec.uml;

import java.util.Collection;

public interface Profile extends Package, MofObject {

    Collection<ElementImport> getMetaclassReference();

    Collection<PackageImport> getMetamodelReference();
}
