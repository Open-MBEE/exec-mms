package org.openmbee.mms.core.services;

import org.openmbee.mms.core.objects.ElementsSearchResponse;

import java.util.Map;

public interface SearchService {
    ElementsSearchResponse basicSearch(String projectId, String refId, Map<String, String> params);

    ElementsSearchResponse recursiveSearch(String projectId, String refId, Map<String, String> params, Map<String, String> recurse, Integer from, Integer size);
}
