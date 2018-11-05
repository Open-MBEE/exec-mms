package org.openmbee.spec.uml;

public interface ClearAssociationAction extends Action, MofObject {

    Association getAssociation();

    InputPin getObject();
}
