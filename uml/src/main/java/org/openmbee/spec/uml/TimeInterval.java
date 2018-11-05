package org.openmbee.spec.uml;

public interface TimeInterval extends Interval, MofObject {

    TimeExpression getMax();

    TimeExpression getMin();
}
