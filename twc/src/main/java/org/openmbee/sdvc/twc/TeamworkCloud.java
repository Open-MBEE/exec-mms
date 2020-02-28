package org.openmbee.sdvc.twc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamworkCloud {
    private String url;
    private String protocol;
    private String port;
    private List<String> aliases;
    private Set<String> knownNames;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }


    synchronized public Set<String> getKnownNames() {
        if(knownNames == null){
            knownNames = new HashSet<>();
            knownNames.add(url.toLowerCase());
            if(aliases != null) {
                aliases.stream().map(String::toLowerCase).forEach(v -> knownNames.add(v));
            }
        }
        return knownNames;
    }

    public boolean hasKnownName(String name) {
        return getKnownNames().contains(name.toLowerCase());
    }

}
