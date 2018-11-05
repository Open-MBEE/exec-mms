package org.openmbee.spec.uml;

import java.util.Collection;

public interface Package extends PackageableElement, TemplateableElement, Namespace, MofObject {

    String getURI();

    Collection<Package> getNestedPackage();

    Package getNestingPackage();

    Collection<Stereotype> getOwnedStereotype();

    Collection<Type> getOwnedType();

    Collection<PackageMerge> getPackageMerge();

    Collection<PackageableElement> getPackagedElement();

    Collection<ProfileApplication> getProfileApplication();
}
