package oracle.mobile.cloud.sample.fif.technician.application.alcl;

import java.util.logging.Level;

import oracle.adfmf.application.LifeCycleListener;
import oracle.adfmf.application.PushNotificationConfig;
import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.framework.event.EventSource;
import oracle.adfmf.framework.event.EventSourceFactory;
import oracle.adfmf.util.Utility;

import oracle.mobile.cloud.sample.fif.technician.application.push.FiFPushHandler;

/**
 * @author Frank Nimphius
 * @copyright Copyright (c) 2015 Oracle. All rights reserved.
 */
public class FiFLifeCycleListenerImpl implements LifeCycleListener, PushNotificationConfig {
    public FiFLifeCycleListenerImpl() {
    }

    /**
     * For the FIF technician application, the application start() method reads from the application preferences and overrides
     * the default configuration. This way the FIF Technician application can be quickly changed to run against other installations
     * of the FIF Technician MBE
     */
    public void start() {
        
        String fifMobileBackendURL =
            (String) AdfmfJavaUtilities.getELValue("#{preferenceScope.application.mcs.fifMobileBackendURL}");
        Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "lifecycleListener::start()",
                                       "Mobile Backend Id = " + fifMobileBackendURL);
       

        //change value of application REST connection.
        if (fifMobileBackendURL != null && fifMobileBackendURL.length() > 0) {
            
            //removes any persisted connection configuration overrides that have been previously set for the FiFMBE key using
            //either overrideConnectionProperty or updateSecurityConfigWithURLParameters. This also clears the connections
            //previously loaded from connections.xml. Its a recommended precaution in the AdfmfJavaUtilities JavaDoc
            AdfmfJavaUtilities.clearSecurityConfigOverrides("FiFMBE");
            
            AdfmfJavaUtilities.overrideConnectionProperty("FiFMBE", "urlconnection", "url", fifMobileBackendURL);
        }
    
    
        String oauthHost = (String) AdfmfJavaUtilities.getELValue("#{preferenceScope.application.oauth.oauthHost}");
        String oauthTokenEndpointURI = (String) AdfmfJavaUtilities.getELValue("#{preferenceScope.application.oauth.oauthTokenEndpointUri}");
        
        Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "lifecycleListener::start()",
                                       "OAUTH HOST = " + oauthHost +" Token Endpoint URI = "+oauthTokenEndpointURI);
               

        //change value of OAUTH REST connection.
        if (oauthHost != null && oauthHost.length() > 0 &&
            oauthTokenEndpointURI != null && oauthTokenEndpointURI.length() > 0 ) {
            
            AdfmfJavaUtilities.clearSecurityConfigOverrides("OAUTH");
            
            AdfmfJavaUtilities.overrideConnectionProperty("OAUTH", "urlconnection", "url", oauthHost);
        }
        
        EventSource evtSource = EventSourceFactory.getEventSource(EventSourceFactory.NATIVE_PUSH_NOTIFICATION_REMOTE_EVENT_SOURCE_NAME);
        evtSource.addListener(new FiFPushHandler());
    }


    public void stop() {
        // Add code here...
    }


    public void activate() {
        // Add code here...
    }

    public void deactivate() {
        // Add code here...
    }


    /* PUSH NOTIFICATION REGISTRATION */

    /*
     * Implement methods of the "PushNotificationConfig" interface. 
     */

    @Override
    public long getNotificationStyle() {
        // Allow for alerts and badging and sounds
        return PushNotificationConfig.NOTIFICATION_STYLE_ALERT | PushNotificationConfig.NOTIFICATION_STYLE_BADGE | PushNotificationConfig.NOTIFICATION_STYLE_SOUND;
    }

    @Override
    public String getSourceAuthorizationId() {
        // Return the GCM sender id. This information is saved in the preferences
        return (String) AdfmfJavaUtilities.getELValue("#{preferenceScope.application.push.gcmSenderId}");
    }
}
