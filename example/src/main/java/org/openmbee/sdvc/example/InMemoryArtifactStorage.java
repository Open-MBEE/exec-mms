package org.openmbee.sdvc.example;

import org.openmbee.sdvc.artifacts.storage.ArtifactStorage;
import org.openmbee.sdvc.json.ElementJson;

import java.util.HashMap;
import java.util.Map;

public class InMemoryArtifactStorage implements ArtifactStorage {

    private static Map<String, byte[]> artifacts = new HashMap<>();

    @Override
    public byte[] get(String location, ElementJson element, String mimetype) {
        return artifacts.get(location);
    }

    @Override
    public String store(byte[] data, ElementJson element, String mimetype) {
        String location = buildLocation(element, mimetype);
        artifacts.put(location, data);
        return location;
    }

    private String buildLocation(ElementJson element, String mimetype) {
        return String.format("%s//%s", element.getId(), mimetype);
    }
}
