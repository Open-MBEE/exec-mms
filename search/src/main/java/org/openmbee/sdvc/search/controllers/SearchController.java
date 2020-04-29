package org.openmbee.sdvc.search.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.exceptions.*;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.core.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
@Tag(name = "Search")
public class SearchController {
    private final Logger logger = LogManager.getLogger(getClass());

    private MethodSecurityService mss;
    private SearchService searchService;

    @Autowired
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @GetMapping(value = "/search")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getBasicSearch(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params
    ) {
         ElementsResponse res = searchService.basicSearch(projectId, refId, params);
         handleSingleResponse(res);
         return res;
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse postBasicSearch(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody BasicSearchRequest request
    ) {
        ElementsResponse res = searchService.recursiveSearch(projectId, refId, request.getParams(), request.getRecurse());
        handleSingleResponse(res);
        return res;
    }


    private void handleSingleResponse(BaseResponse res) {
        if (res.getRejected() != null && !res.getRejected().isEmpty()) {
            List<Rejection> rejected = res.getRejected();
            int code = rejected.get(0).getCode();
            switch(code) {
                case 304:
                    throw new NotModifiedException(res);
                case 400:
                    throw new BadRequestException(res);
                case 401:
                    throw new UnauthorizedException(res);
                case 403:
                    throw new ForbiddenException(res);
                case 404:
                    throw new NotFoundException(res);
                case 410:
                    throw new DeletedException(res);
                default:
                    break;
            }
        }
    }
}
