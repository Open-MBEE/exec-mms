package org.openmbee.spec.uml;

public interface StructuralFeatureAction extends Action, MofObject {

    InputPin getObject();

    StructuralFeature getStructuralFeature();
}
