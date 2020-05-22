package org.openmbee.sdvc.artifacts.intercept;

import org.openmbee.sdvc.artifacts.ArtifactConstants;
import org.openmbee.sdvc.artifacts.json.ArtifactJson;
import org.openmbee.sdvc.artifacts.objects.ArtifactResponse;
import org.openmbee.sdvc.artifacts.service.ArtifactService;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.intercept.SdvcHandlerInterceptorAdapter;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.crud.controllers.elements.ElementsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArtifactsInterceptor extends SdvcHandlerInterceptorAdapter {

    private Pattern uriPattern = Pattern.compile(".*/projects/([^/]+)/refs/([^/]+)/elements/([^/]+)");

    private ArtifactService artifactService;

    private MethodSecurityService mss;

    @Autowired
    public void setArtifactService(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            if(ElementsController.class.equals(handlerMethod.getBeanType()) && "getElement".equals(handlerMethod.getMethod().getName())) {
                String acceptHeader = request.getHeader("Accept");
                //Let the request through to the ElementsController if the Accept header is empty or json
                if(acceptHeader == null || acceptHeader.isEmpty() || acceptHeader.endsWith("json") || acceptHeader.equals(MediaType.ALL_VALUE)) {
                    return true;
                }

                //Handle as an artifact request
                Matcher matcher = uriPattern.matcher(request.getRequestURI());
                if(matcher.matches()) {
                    String projectId = matcher.group(1);
                    String refId = matcher.group(2);
                    String elementId = matcher.group(3);

                    if(!mss.hasBranchPrivilege(SecurityContextHolder.getContext().getAuthentication(), projectId, refId,
                        "BRANCH_READ", true)) {
                        response.setStatus(403);
                        return false;
                    }

                    Map<String, String> params = request.getParameterMap().entrySet().stream().collect(
                        Collectors.toMap(v -> v.getKey(), v -> v.getValue().length > 0 ? v.getValue()[0] : ""));
                    params.put(ArtifactConstants.MIMETYPE_PARAM, acceptHeader);
                    ArtifactResponse artifact = artifactService.get(projectId, refId, elementId, params);
                    if(artifact != null && artifact.getData() != null) {
                        response.setContentType(artifact.getMimeType());
                        response.setContentLength(artifact.getData().length);
                        response.setStatus(HttpStatus.OK.value());
                        response.getOutputStream().write(artifact.getData());
                        response.getOutputStream().flush();
                        response.getOutputStream().close();
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                    return false;
                }
            }
        }
        //Allow the request to go through to the original handler
        return true;
    }

    @Override
    public void register(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns().addPathPatterns("/projects/*/refs/*/elements/*");
    }
}
