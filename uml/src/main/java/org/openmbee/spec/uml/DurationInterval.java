package org.openmbee.spec.uml;

public interface DurationInterval extends Interval, MofObject {

    Duration getMax();

    Duration getMin();
}
