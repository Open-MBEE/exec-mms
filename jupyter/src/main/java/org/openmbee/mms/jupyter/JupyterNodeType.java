package org.openmbee.mms.jupyter;

import java.util.HashMap;
import java.util.Map;

public enum JupyterNodeType {
    NOTEBOOK(1),
    CELL(2);

    private static Map<Integer, JupyterNodeType> map = new HashMap<>();

    static {
        for (JupyterNodeType n : JupyterNodeType.values()) {
            map.put(n.value, n);
        }
    }

    private int value;

    JupyterNodeType(int value) {
        this.value = value;
    }

    public static JupyterNodeType valueOf(int n) {
        return map.get(n);
    }

    public int getValue() {
        return value;
    }
}


