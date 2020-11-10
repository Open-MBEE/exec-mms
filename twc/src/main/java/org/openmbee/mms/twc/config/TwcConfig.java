package org.openmbee.mms.twc.config;

import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.security.TwcAuthenticationProvider;
import org.openmbee.mms.twc.utilities.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("twc")
public class TwcConfig {

    @Autowired
    private RestUtils restUtils;

    private List<TeamworkCloud> instances;

    public List<TeamworkCloud> getInstances() {
        return instances;
    }

    public void setInstances(List<TeamworkCloud> instances) {
        this.instances = instances;
    }

    public TwcAuthenticationProvider getAuthNProvider(String associatedTWC) {
        if(associatedTWC == null)
            return null;

        for(TeamworkCloud twc : getInstances()){
            if(twc.hasKnownName(associatedTWC)){
                return new TwcAuthenticationProvider(restUtils, twc);
            }
        }
        return null;
    }

    public TeamworkCloud getTeamworkCloud(String twcUrl) {
        for(TeamworkCloud twc : getInstances()) {
            if (twc.hasKnownName(twcUrl)) {
                return twc;
            }
        }
        return null;
    }
}
