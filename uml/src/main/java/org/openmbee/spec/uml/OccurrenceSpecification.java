package org.openmbee.spec.uml;

import java.util.Collection;

public interface OccurrenceSpecification extends InteractionFragment, MofObject {
    // Collection<Lifeline> getCovered();

    Collection<GeneralOrdering> getToAfter();

    Collection<GeneralOrdering> getToBefore();
}
