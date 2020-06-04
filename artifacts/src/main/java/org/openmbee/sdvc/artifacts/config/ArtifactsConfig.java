package org.openmbee.sdvc.artifacts.config;

import org.openmbee.sdvc.artifacts.pubsub.ArtifactsElementsHookSubscriber;
import org.openmbee.sdvc.core.pubsub.EmbeddedHookSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtifactsConfig {
    @Bean
    public EmbeddedHookSubscriber getArtifactsPreCommitSubscriber() {
        return new ArtifactsElementsHookSubscriber();
    }

}
