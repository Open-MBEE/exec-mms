package org.openmbee.mms.twc.exceptions;

import org.openmbee.mms.core.exceptions.MMSException;
import org.springframework.http.HttpStatus;

public class TwcConfigurationException extends MMSException {
    public TwcConfigurationException(HttpStatus code, Object messageObject) {
        super(code, messageObject);
    }

    public TwcConfigurationException(int code, Object messageObject) {
        super(code, messageObject);
    }
}
