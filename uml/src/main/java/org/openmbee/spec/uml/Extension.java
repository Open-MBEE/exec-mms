package org.openmbee.spec.uml;

public interface Extension extends Association, MofObject {

    Boolean isRequired();

    Class getMetaclass();

    // List<ExtensionEnd> getOwnedEnd();
}
