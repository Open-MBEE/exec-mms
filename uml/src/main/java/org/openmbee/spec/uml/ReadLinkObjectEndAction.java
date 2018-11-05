package org.openmbee.spec.uml;

public interface ReadLinkObjectEndAction extends Action, MofObject {

    Property getEnd();

    InputPin getObject();

    OutputPin getResult();
}
