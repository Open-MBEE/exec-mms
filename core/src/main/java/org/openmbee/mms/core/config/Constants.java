package org.openmbee.mms.core.config;

import java.util.*;
import java.util.regex.Pattern;

public class Constants {

    public static final String ORGANIZATION_KEY = "orgs";
    public static final String PROJECT_KEY = "projects";
    public static final String BRANCH_KEY = "refs";
    public static final String ELEMENT_KEY = "elements";
    public static final String COMMIT_KEY = "commits";
    public static final String WEBHOOK_KEY = "webhooks";

    public static final String REJECTED = "rejected";
    public static final String MESSAGES = "messages";
    public static final String CODE = "code";

    public static final String MASTER_BRANCH = "master";

    public static final Pattern BRANCH_ID_VALID_PATTERN = Pattern.compile("^[\\w-]+$");

    public static final Map<String, List> RPmap = new LinkedHashMap<>();
    public static final List<String> aPriv;
    public static final List<String> rPriv;
    public static final List<String> wPriv;
    static {
        aPriv = Arrays.asList("ORG_READ", "ORG_EDIT", "ORG_UPDATE_PERMISSIONS", "ORG_READ_PERMISSIONS", "ORG_CREATE_PROJECT", "ORG_DELETE", "PROJECT_READ", "PROJECT_EDIT", "PROJECT_READ_COMMITS", "PROJECT_CREATE_BRANCH", "PROJECT_DELETE", "PROJECT_UPDATE_PERMISSIONS", "PROJECT_READ_PERMISSIONS", "PROJECT_CREATE_WEBHOOKS", "BRANCH_READ", "BRANCH_EDIT_CONTENT", "BRANCH_DELETE", "BRANCH_UPDATE_PERMISSIONS", "BRANCH_READ_PERMISSIONS");
        rPriv = Arrays.asList("ORG_READ", "ORG_READ_PERMISSIONS", "PROJECT_READ", "PROJECT_READ_COMMITS", "PROJECT_READ_PERMISSIONS", "BRANCH_READ", "BRANCH_READ_PERMISSIONS");
        wPriv = Arrays.asList("ORG_READ", "ORG_EDIT", "ORG_READ_PERMISSIONS", "ORG_CREATE_PROJECT", "PROJECT_READ", "PROJECT_EDIT", "PROJECT_READ_COMMITS", "PROJECT_CREATE_BRANCH", "PROJECT_READ_PERMISSIONS", "PROJECT_CREATE_WEBHOOKS", "BRANCH_READ", "BRANCH_EDIT_CONTENT", "BRANCH_READ_PERMISSIONS");
        RPmap.put("ADMIN", aPriv);
        RPmap.put("READER", rPriv);
        RPmap.put("WRITER", wPriv);
    }
}
