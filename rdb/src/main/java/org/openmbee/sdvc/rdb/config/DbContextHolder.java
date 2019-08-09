package org.openmbee.sdvc.rdb.config;

public class DbContextHolder {

    private static final ThreadLocal<ContextObject> contextHolder = new ThreadLocal<>();

    private DbContextHolder() {
    }

    public static ContextObject getContext() {
        return contextHolder.get() != null ? contextHolder.get() : new ContextObject();
    }

    public static void setContext(String projectId) {
        contextHolder.set(new ContextObject(projectId));
    }

    public static void setContext(String projectId, String refId) {
        contextHolder.set(new ContextObject(projectId, refId));
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
