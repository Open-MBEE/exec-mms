package org.openmbee.spec.uml;

import java.util.List;

public interface ReplyAction extends Action, MofObject {

    Trigger getReplyToCall();

    List<InputPin> getReplyValue();

    InputPin getReturnInformation();
}
