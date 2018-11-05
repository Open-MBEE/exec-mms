package org.openmbee.spec.uml;

public interface AddStructuralFeatureValueAction extends WriteStructuralFeatureAction, MofObject {

    InputPin getInsertAt();

    Boolean isReplaceAll();
}
