package org.openmbee.sdvc.core.validation;

import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PreCommitService {

    private List<PreCommitSubscriber> preCommitSubscribers;

    @Autowired
    public void setPreCommitSubscribers(List<PreCommitSubscriber> preCommitSubscribers) {
        this.preCommitSubscribers = preCommitSubscribers;
    }

    public void preCommitElementChanges(String projectId, String refId, List<ElementJson> elements, Map<String, String> params, String user) {
        if(preCommitSubscribers == null || elements == null) {
            return;
        }
        for(PreCommitSubscriber s : preCommitSubscribers) {
            s.preCommitElementChanges(projectId, refId, elements, params, user);
        }
    }
}
