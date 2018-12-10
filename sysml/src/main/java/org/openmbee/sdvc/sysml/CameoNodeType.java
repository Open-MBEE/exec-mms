package org.openmbee.sdvc.sysml;

import java.util.HashMap;
import java.util.Map;

public enum CameoNodeType {
    ELEMENT(1),
    SITE(2),
    PROJECT(3),
    DOCUMENT(4),
    COMMENT(5),
    CONSTRAINT(6),
    INSTANCESPECIFICATION(7),
    OPERATION(8),
    PACKAGE(9),
    PROPERTY(10),
    PARAMETER(11),
    VIEW(12),
    VIEWPOINT(13),
    SITEANDPACKAGE(14),
    HOLDINGBIN(15),
    MOUNT(16);

    private static Map<Integer, CameoNodeType> map = new HashMap<>();

    static {
        for (CameoNodeType n : CameoNodeType.values()) {
            map.put(n.value, n);
        }
    }

    private int value;

    CameoNodeType(int value) {
        this.value = value;
    }

    public static CameoNodeType valueOf(int n) {
        return map.get(n);
    }

    public int getValue() {
        return value;
    }
}


