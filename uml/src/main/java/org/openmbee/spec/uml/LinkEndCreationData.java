package org.openmbee.spec.uml;

public interface LinkEndCreationData extends LinkEndData, MofObject {

    InputPin getInsertAt();

    Boolean isReplaceAll();
}
