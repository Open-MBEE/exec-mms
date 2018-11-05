package org.openmbee.spec.uml;

public interface Abstraction extends Dependency, MofObject {

    OpaqueExpression getMapping();
}
