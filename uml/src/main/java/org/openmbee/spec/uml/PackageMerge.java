package org.openmbee.spec.uml;

public interface PackageMerge extends DirectedRelationship, MofObject {

    Package getMergedPackage();

    Package getReceivingPackage();
}
