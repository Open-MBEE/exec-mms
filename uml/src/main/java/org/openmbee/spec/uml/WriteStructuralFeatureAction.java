package org.openmbee.spec.uml;

public interface WriteStructuralFeatureAction extends StructuralFeatureAction, MofObject {

    OutputPin getResult();

    InputPin getValue();
}
