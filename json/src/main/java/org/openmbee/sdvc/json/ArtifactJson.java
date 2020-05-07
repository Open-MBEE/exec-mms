package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

@Schema(name = "Artifact")
@JsonIgnoreProperties({"empty"})
public class ArtifactJson extends HashMap<String, Object> {

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
}
