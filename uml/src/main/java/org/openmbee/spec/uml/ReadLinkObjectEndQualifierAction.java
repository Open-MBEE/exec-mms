package org.openmbee.spec.uml;

public interface ReadLinkObjectEndQualifierAction extends Action, MofObject {

    InputPin getObject();

    Property getQualifier();

    OutputPin getResult();
}
