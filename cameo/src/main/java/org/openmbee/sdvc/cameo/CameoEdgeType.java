package org.openmbee.sdvc.cameo;

import java.util.HashMap;
import java.util.Map;

public enum CameoEdgeType {

    CONTAINMENT(1),
    VIEW(2),
    TRANSCLUSION(3),
    CHILDVIEW(4);

    private static Map<Integer, CameoEdgeType> map = new HashMap<>();

    static {
        for (CameoEdgeType e : CameoEdgeType.values()) {
            map.put(e.value, e);
        }
    }

    private int value;

    CameoEdgeType(int value) {
        this.value = value;
    }

    public static CameoEdgeType valueOf(int e) {
        return map.get(e);
    }

    public int getValue() {
        return value;
    }
}
