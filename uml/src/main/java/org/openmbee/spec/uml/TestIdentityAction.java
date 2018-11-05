package org.openmbee.spec.uml;

public interface TestIdentityAction extends Action, MofObject {

    InputPin getFirst();

    OutputPin getResult();

    InputPin getSecond();
}
