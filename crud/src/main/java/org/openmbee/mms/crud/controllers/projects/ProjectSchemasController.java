package org.openmbee.mms.crud.controllers.projects;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.core.objects.SchemasResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schemas")
@Tag(name = "Projects")
public class ProjectSchemasController {

    ProjectSchemas schemas;

    @Autowired
    public ProjectSchemasController(ProjectSchemas schemas) {
        this.schemas = schemas;
    }

    @GetMapping
    @SecurityRequirements(value = {})
    public SchemasResponse getProjectSchemaOptions() {
        return new SchemasResponse().addAll(schemas.getSchemas().values());
    }
}
