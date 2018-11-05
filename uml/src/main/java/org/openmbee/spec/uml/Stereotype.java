package org.openmbee.spec.uml;

import java.util.Collection;

public interface Stereotype extends Class, MofObject {

    Collection<Image> getIcon();

    Profile getProfile();
}
