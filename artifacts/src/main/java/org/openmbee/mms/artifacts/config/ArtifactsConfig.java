package org.openmbee.mms.artifacts.config;

import org.openmbee.mms.artifacts.pubsub.ArtifactsElementsHookSubscriber;
import org.openmbee.mms.core.pubsub.EmbeddedHookSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtifactsConfig {
    @Bean
    public EmbeddedHookSubscriber getArtifactsPreCommitSubscriber() {
        return new ArtifactsElementsHookSubscriber();
    }

}
