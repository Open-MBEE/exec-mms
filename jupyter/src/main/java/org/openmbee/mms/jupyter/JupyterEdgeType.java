package org.openmbee.mms.jupyter;

import java.util.HashMap;
import java.util.Map;

public enum JupyterEdgeType {

    CONTAINMENT(1);

    private static Map<Integer, JupyterEdgeType> map = new HashMap<>();

    static {
        for (JupyterEdgeType e : JupyterEdgeType.values()) {
            map.put(e.value, e);
        }
    }

    private int value;

    JupyterEdgeType(int value) {
        this.value = value;
    }

    public static JupyterEdgeType valueOf(int e) {
        return map.get(e);
    }

    public int getValue() {
        return value;
    }
}
