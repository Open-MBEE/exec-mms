package org.openmbee.sdvc.core.validation;

import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.json.ElementJson;

import java.util.List;
import java.util.Map;

public interface PreCommitSubscriber {
    void preCommitElementChanges(String projectId, String refId, List<ElementJson> elements, Map<String, String> params, String user);
}
