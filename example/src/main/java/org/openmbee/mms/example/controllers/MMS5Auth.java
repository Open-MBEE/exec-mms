package org.openmbee.mms.example.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.openmbee.mms.authenticator.security.JwtAuthenticationRequest;
import org.openmbee.mms.authenticator.security.JwtAuthenticationResponse;
import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.BranchGroupPerm;
import org.openmbee.mms.data.domains.global.BranchUserPerm;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.data.domains.global.ProjectGroupPerm;
import org.openmbee.mms.data.domains.global.ProjectUserPerm;
import org.openmbee.mms.rdb.repositories.BranchGroupPermRepository;
import org.openmbee.mms.rdb.repositories.BranchRepository;
import org.openmbee.mms.rdb.repositories.BranchUserPermRepository;
import org.openmbee.mms.rdb.repositories.ProjectGroupPermRepository;
import org.openmbee.mms.rdb.repositories.ProjectRepository;
import org.openmbee.mms.rdb.repositories.ProjectUserPermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("/mms5auth")
public class MMS5Auth {
    private AuthenticationManager authenticationManager;
    private ProjectUserPermRepository projectUserPerms;
    private ProjectGroupPermRepository projectGroupPerms;
    private BranchUserPermRepository branchUserPerms;
    private BranchGroupPermRepository branchGroupPerms;
    private ProjectRepository projectRepo;
    private BranchRepository branchRepo;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    public MMS5Auth(AuthenticationManager authenticationManager,
        ProjectGroupPermRepository projectGroupPerms,
        ProjectUserPermRepository projectUserPerms,
        BranchUserPermRepository branchUserPerms,
        BranchGroupPermRepository branchGroupPerms,
        ProjectRepository projectRepo,
        BranchRepository branchRepo) {
        this.authenticationManager = authenticationManager;
        this.projectUserPerms = projectUserPerms;
        this.projectGroupPerms = projectGroupPerms;
        this.branchUserPerms = branchUserPerms;
        this.branchGroupPerms = branchGroupPerms;
        this.projectRepo = projectRepo;
        this.branchRepo = branchRepo;

    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements(value = {})
    @Transactional(readOnly = true)
    public JwtAuthenticationResponse createAuthenticationToken(
        @RequestBody JwtAuthenticationRequest authenticationRequest) {
        final Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> groups = authentication.getAuthorities();

        Map<String, Object> o = new HashMap<>();
        Map<String, Object> perms = new HashMap<>();
        o.put("permissions", perms);
        o.put("sub", userDetails.getUsername());

        Map<String, Map<String, Object>> projectPerms = new HashMap<>();
        for (ProjectUserPerm perm: projectUserPerms.findAllByUser_Username(userDetails.getUsername())) {
            String projectId = perm.getProject().getProjectId();
            String role = perm.getRole().getName();
            updateProjectPerm(projectPerms, projectId, role);
            Map<String, Map<String, Object>> branchPerms = new HashMap<>();
            for (BranchUserPerm perm2: branchUserPerms.findAllByUser_UsernameAndBranch_Project_ProjectId(userDetails.getUsername(), projectId)) {
                updateProjectPerm(branchPerms, perm2.getBranch().getBranchId(), perm2.getRole().getName());
            }
            projectPerms.get(projectId).put("branches", branchPerms);
        }
        for (GrantedAuthority group: groups) {
            for (ProjectGroupPerm perm: projectGroupPerms.findAllByGroup_Name(group.getAuthority())) {
                String projectId = perm.getProject().getProjectId();
                String role = perm.getRole().getName();
                updateProjectPerm(projectPerms, projectId, role);
                if (!(projectPerms.get(projectId)).containsKey("branches")){
                    (projectPerms.get(projectId)).put("branches", new HashMap<String, Map<String, Object>>());
                }
                Map<String, Map<String, Object>> branchPerms = (Map<String, Map<String, Object>>) (projectPerms.get(projectId)).get("branches");
                for (BranchGroupPerm perm2: branchGroupPerms.findAllByGroup_NameAndBranch_Project_ProjectId(group.getAuthority(), projectId)) {
                    updateProjectPerm(branchPerms, perm2.getBranch().getBranchId(), perm2.getRole().getName());
                }
            }
        }
        for (Map<String, Object> projectPerm: projectPerms.values()) {
            projectPerm.put("branches", ((Map<String, Object>)projectPerm.get("branches")).values());
        }
        perms.put("projects", projectPerms.values());
        final String token = generateToken(o);
        return new JwtAuthenticationResponse(token);
    }

    @GetMapping
    @SecurityRequirements(value = {})
    @Transactional(readOnly = true)
    public JwtAuthenticationResponse createAnonAuthenticationToken() {
        String username = "anonymous";
        Map<String, Object> o = new HashMap<>();
        Map<String, Object> perms = new HashMap<>();
        o.put("permissions", perms);
        o.put("sub", username);
        List<Project> publicProjects = projectRepo.findAllByIsPublicTrue();
        Map<String, Map<String, Object>> projectPerms = new HashMap<>();
        for (Project project: publicProjects) {
            updateProjectPerm(projectPerms, project.getProjectId(), "READER");
            Map<String, Map<String, Object>> branchPerms = new HashMap<>();
            for (Branch branch: branchRepo.findAllByProject_ProjectId(project.getProjectId())) {
                updateProjectPerm(branchPerms, branch.getBranchId(), "READER");
            }
            projectPerms.get(project.getProjectId()).put("branches", branchPerms);
        }
        for (Map<String, Object> projectPerm: projectPerms.values()) {
            projectPerm.put("branches", ((Map<String, Object>)projectPerm.get("branches")).values());
        }
        perms.put("projects", projectPerms.values());
        final String token = generateToken(o);
        return new JwtAuthenticationResponse(token);
    }

    private void updateProjectPerm(Map<String, Map<String, Object>> projectPerms, String projectId, String role) {
        if (!projectPerms.containsKey(projectId)) {
            projectPerms.put(projectId, new HashMap<>());
        }
        Map<String, Object> projectPerm = projectPerms.get(projectId);
        projectPerm.put("id", projectId);
        String existingRole = (String) projectPerm.get("role");
        role = existingRole == null || existingRole.equals("READER") || role.equals("ADMIN") ? role : existingRole;
        projectPerm.put("role", role);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
            .signWith(getSecretKey())
            .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }
}
