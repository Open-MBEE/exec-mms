package org.openmbee.mms.jupyter.services;

import org.openmbee.mms.crud.services.DefaultProjectService;
import org.openmbee.mms.core.services.ProjectService;
import org.springframework.stereotype.Service;


@Service("jupyterProjectService")
public class JupyterProjectService extends DefaultProjectService implements ProjectService {

}
