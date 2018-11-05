package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Interaction extends InteractionFragment, Behavior, MofObject {

    Collection<Action> getAction();

    Collection<Gate> getFormalGate();

    List<InteractionFragment> getFragment();

    Collection<Lifeline> getLifeline();

    Collection<Message> getMessage();
}
