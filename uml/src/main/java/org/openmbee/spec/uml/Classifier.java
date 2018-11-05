package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Classifier extends Namespace, Type, TemplateableElement, RedefinableElement,
    MofObject {

    List<Property> getAttribute();

    Collection<CollaborationUse> getCollaborationUse();

    Collection<Feature> getFeature();

    Collection<Classifier> getGeneral();

    Collection<Generalization> getGeneralization();

    Collection<NamedElement> getInheritedMember();

    Boolean isAbstract();

    Boolean isFinalSpecialization();

    RedefinableTemplateSignature getOwnedTemplateSignature();

    Collection<UseCase> getOwnedUseCase();

    Collection<GeneralizationSet> getPowertypeExtent();

    Collection<Classifier> getRedefinedClassifier();

    CollaborationUse getRepresentation();

    Collection<Substitution> getSubstitution();

    ClassifierTemplateParameter getTemplateParameter();

    Collection<UseCase> getUseCase();
}
