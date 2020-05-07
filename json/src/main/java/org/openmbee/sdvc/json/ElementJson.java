package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Schema(name = "Element")
public class ElementJson extends BaseJson<ElementJson> {

    public static final String INREFIDS = "_inRefIds";
    public static final String ARTIFACTS = "_artifacts";

    @JsonProperty(INREFIDS)
    @Schema(accessMode = AccessMode.READ_ONLY)
    public List<String> getInRefIds() {
        return (List<String>) this.get(INREFIDS);
    }

    @JsonProperty(INREFIDS)
    public ElementJson setInRefIds(List<String> inRefIds) {
        this.put(INREFIDS, inRefIds);
        return this;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty(ARTIFACTS)
    @Schema(accessMode = AccessMode.READ_ONLY)
    public List<ArtifactJson> getArtifacts() {
        //TODO: This method is pretty dirty, would be good to find a better way to manage correct types for the nested artifacts...
        List<Object> rawArtifacts = (List)this.get(ARTIFACTS);
        if(rawArtifacts == null) {
            return null;
        }
        if(rawArtifacts.size() == 0 || rawArtifacts.get(0) instanceof ArtifactJson){
            return (List)rawArtifacts;
        }
        if(rawArtifacts.get(0) instanceof Map) {
            List<ArtifactJson> artifacts = rawArtifacts.stream().map(v -> new ArtifactJson((Map<String, Object>) v)).collect(Collectors.toList());
            setArtifacts(artifacts);
            return artifacts;
        }
        return null;
    }

    @JsonProperty(ARTIFACTS)
    public ElementJson setArtifacts(List<ArtifactJson> artifacts) {
        this.put(ARTIFACTS, artifacts);
        return this;
    }

}
