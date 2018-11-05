package org.openmbee.spec.uml;

import java.util.Collection;

public interface Element extends MofObject {

    Collection<Comment> getOwnedComment();

    Collection<Element> getOwnedElement();

    Element getOwner();
}
