package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.ElementsResponse;

import java.util.Map;

public interface SearchService {
    ElementsResponse basicSearch(String projectId, String refId, Map<String, String> params);
}
