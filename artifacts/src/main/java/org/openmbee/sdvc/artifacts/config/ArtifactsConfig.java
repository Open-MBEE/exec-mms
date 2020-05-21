package org.openmbee.sdvc.artifacts.config;

import org.openmbee.sdvc.artifacts.intercept.ArtifactsInterceptor;
import org.openmbee.sdvc.artifacts.validation.ArtifactsPreCommitSubscriber;
import org.openmbee.sdvc.core.intercept.SdvcHandlerInterceptorAdapter;
import org.openmbee.sdvc.core.validation.PreCommitSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtifactsConfig {
    @Bean
    public PreCommitSubscriber getArtifactsPreCommitSubscriber() {
        return new ArtifactsPreCommitSubscriber();
    }

    @Bean
    public SdvcHandlerInterceptorAdapter getArtifactInterceptor() {
        return new ArtifactsInterceptor();
    }
}
