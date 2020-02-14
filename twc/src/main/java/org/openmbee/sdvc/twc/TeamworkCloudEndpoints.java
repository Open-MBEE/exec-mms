package org.openmbee.sdvc.twc;

public enum TeamworkCloudEndpoints {
    LOGIN("login");

    private String path;

    public String getPath() {
        return path;
    }

    TeamworkCloudEndpoints(String path){
        this.path = path;
    }

    public String buildUrl(TeamworkCloud twc){
        return String.format("%s://%s:%s/osmc/%s", twc.getProtocol(), twc.getUrl(), twc.getPort(), getPath());
    }
}