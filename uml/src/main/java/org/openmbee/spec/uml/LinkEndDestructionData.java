package org.openmbee.spec.uml;

public interface LinkEndDestructionData extends LinkEndData, MofObject {

    InputPin getDestroyAt();

    Boolean isDestroyDuplicates();
}
