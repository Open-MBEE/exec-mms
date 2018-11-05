package org.openmbee.spec.uml;

public interface RemoveStructuralFeatureValueAction extends WriteStructuralFeatureAction,
    MofObject {

    Boolean isRemoveDuplicates();

    InputPin getRemoveAt();
}
