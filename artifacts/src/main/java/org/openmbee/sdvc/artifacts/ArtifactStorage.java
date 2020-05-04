package org.openmbee.sdvc.artifacts;

import org.openmbee.sdvc.json.ElementJson;

public interface ArtifactStorage {

    //TODO: decide between using byte[] or Input/Output Streams
    byte[] get(String location, ElementJson element, String mimetype);
    //returns location
    String store(byte[] data, ElementJson element, String mimetype);
}
