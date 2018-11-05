package org.openmbee.spec.uml;

public interface ExpansionNode extends ObjectNode, MofObject {

    ExpansionRegion getRegionAsInput();

    ExpansionRegion getRegionAsOutput();
}
