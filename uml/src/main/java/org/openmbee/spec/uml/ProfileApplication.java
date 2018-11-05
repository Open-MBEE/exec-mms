package org.openmbee.spec.uml;

public interface ProfileApplication extends DirectedRelationship, MofObject {

    Profile getAppliedProfile();

    Package getApplyingPackage();

    Boolean isStrict();
}
