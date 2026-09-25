package com.phatakp.kpevents.common.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Building {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G");

    private final String code;

    Building(String code) {
        this.code = code;
    }

    // Pre-computed map for fast O(1) character lookup
    private static final Map<String, Building> BY_CODE = new HashMap<>();

    static {
        for (Building building : values()) {
            BY_CODE.put(building.code, building);
        }
    }

    /**
     * Converts a primitive char to the matching Level Enum.
     */
    public static Building fromChar(String code) {
        if (code == null) {return null;}

        Building building = BY_CODE.get(code);
        if (building == null) {
            throw new IllegalArgumentException("Unknown character code: " + code);
        }
        return building;
    }


}
