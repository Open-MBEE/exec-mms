package org.openmbee.mms.artifacts.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openmbee.mms.json.ElementJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Schema(name = "Artifact")
@JsonIgnoreProperties({"empty"})
public class ArtifactJson extends HashMap<String, Object> {

    public static final String ARTIFACTS = "_artifacts";
    public static final String MIMETYPE = "mimetype";
    public static final String EXTENSION = "extension";
    public static final String LOCATION = "location";
    public static final String LOCATIONTYPE = "locationType";

    public ArtifactJson() {
        super();
    }

    public ArtifactJson(Map<String, Object> map) {
        super(map);
    }

    @JsonProperty(MIMETYPE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public String getMimeType() {
        return (String) this.get(MIMETYPE);
    }

    @JsonProperty(MIMETYPE)
    public ArtifactJson setMimeType(String mimeType) {
        this.put(MIMETYPE, mimeType);
        return  this;
    }

    @JsonProperty(EXTENSION)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public String getExtension() {
        return (String) this.get(EXTENSION);
    }

    @JsonProperty(EXTENSION)
    public ArtifactJson setExtension(String extension) {
        this.put(EXTENSION, extension);
        return  this;
    }

    @JsonProperty(LOCATION)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public String getLocation() {
        return (String) this.get(LOCATION);
    }

    @JsonProperty(LOCATION)
    public ArtifactJson setLocation(String location) {
        this.put(LOCATION, location);
        return  this;
    }

    @JsonProperty(LOCATIONTYPE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public String getLocationType() {
        return (String) this.get(LOCATIONTYPE);
    }

    @JsonProperty(LOCATIONTYPE)
    public ArtifactJson setLocationType(String locationType) {
        this.put(LOCATIONTYPE, locationType);
        return  this;
    }

    public static List<ArtifactJson> getArtifacts(ElementJson elementJson){

        List<Object> rawArtifacts = (List)elementJson.get(ARTIFACTS);
        if(rawArtifacts == null || rawArtifacts.isEmpty()) {
            return new ArrayList<>();
        }

        if(rawArtifacts.get(0) instanceof Map) {
            return rawArtifacts.stream().map(v -> new ArtifactJson((Map<String, Object>) v)).collect(Collectors.toList());
        }
        return null;
    }

    public static void setArtifacts(ElementJson elementJson, List<ArtifactJson> artifacts) {
        elementJson.put(ARTIFACTS, artifacts);
    }
}
