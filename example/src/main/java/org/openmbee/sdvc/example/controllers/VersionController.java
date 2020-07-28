package org.openmbee.sdvc.example.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Monitor")
@PropertySource("classpath:/mmsversion.properties")
public class VersionController {
    @Value("${mmsversion}")
    private String mmsVersion;

    @GetMapping(value = "/mmsversion", produces = MediaType.APPLICATION_JSON_VALUE)
    public MmsVersion getMmsVersion() {
        return new MmsVersion(mmsVersion);
    }

    public static class MmsVersion {
        private String mmsVersion;
        public MmsVersion(String mmsVersion) {
            this.mmsVersion = mmsVersion;
        }

        public String getMmsVersion() {
            return mmsVersion;
        }
    }
}
