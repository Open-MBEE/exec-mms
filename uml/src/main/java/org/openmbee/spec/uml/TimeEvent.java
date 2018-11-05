package org.openmbee.spec.uml;

public interface TimeEvent extends Event, MofObject {

    Boolean isRelative();

    TimeExpression getWhen();
}
