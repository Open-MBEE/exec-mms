package org.openmbee.sdvc.search.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.objects.ElementsSearchResponse;
import org.openmbee.sdvc.core.services.SearchService;
import org.openmbee.sdvc.search.objects.BasicSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
@Tag(name = "Search")
public class SearchController {
    private final Logger logger = LogManager.getLogger(getClass());

    private SearchService searchService;

    @Autowired
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = "/search")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsSearchResponse getBasicSearch(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params
    ) {
         return searchService.basicSearch(projectId, refId, params);
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsSearchResponse postBasicSearch(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody BasicSearchRequest request
    ) {
        return searchService.recursiveSearch(projectId, refId, request.getParams(), request.getRecurse(),
            request.getFrom(), request.getSize());
    }

}
