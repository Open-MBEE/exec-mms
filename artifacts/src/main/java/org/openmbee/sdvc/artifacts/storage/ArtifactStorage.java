package org.openmbee.sdvc.artifacts.storage;

import org.openmbee.sdvc.json.ElementJson;

public interface ArtifactStorage {

    byte[] get(String location, ElementJson element, String mimetype);
    //returns location
    String store(byte[] data, ElementJson element, String mimetype);
}
