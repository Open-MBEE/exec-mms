package org.openmbee.spec.uml;

public interface ObjectFlow extends ActivityEdge, MofObject {

    Boolean isMulticast();

    Boolean isMultireceive();

    Behavior getSelection();

    Behavior getTransformation();
}
