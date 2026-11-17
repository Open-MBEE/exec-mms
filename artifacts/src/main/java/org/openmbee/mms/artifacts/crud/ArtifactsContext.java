package org.openmbee.mms.artifacts.crud;

public class ArtifactsContext {
    private static ThreadLocal<Boolean> artifactContext = new ThreadLocal<>();

    public static void setArtifactContext(Boolean isArtifactContext) {
        artifactContext.set(isArtifactContext);
    }

    public static void clearArtifactContext() {
        artifactContext.remove();
    }

    public static boolean isArtifactContext() {
        return Boolean.TRUE.equals(artifactContext.get());
    }
}
