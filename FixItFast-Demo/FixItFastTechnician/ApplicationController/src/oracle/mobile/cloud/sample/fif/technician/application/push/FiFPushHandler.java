package oracle.mobile.cloud.sample.fif.technician.application.push;


//todo implement push
/*
 * NOTE! THIS PART IS WORK IN PROGRESS! THE CODE IS TAKEN FROM THE PUSH DEMO AND NEEDS CHANGES. ESPECIALLY THE
 * REGISTRATION THAT NEEDS TO BE WITHIN THE VIEW CONTROLLER POJECT SHOULD BE AUTOMATED USING THE AUTHENTICATED
 * USERNAME AS AN USERID. ALSO, WONDERING IF MEMORY SCOPE ATTRIBUTE USAGE CAN BE REPLACED WITH A MANAGED BEAN
 * IN APPLICATION SCOPE
 */

import java.util.HashMap;
import java.util.logging.Level;

import javax.el.ValueExpression;

import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.framework.api.JSONBeanSerializationHelper;
import oracle.adfmf.framework.event.Event;
import oracle.adfmf.framework.event.EventListener;
import oracle.adfmf.framework.exception.AdfException;
import oracle.adfmf.util.Utility;

/**
 * The FIF technician application may receive push notification messages from MCS. The push notification is displayed in a note window
 * on top of the current page for the user to then decide whether or not to interrup his/her work to check the notification. Alternatively
 * the user can ignore the message and later query the list of incidents for the "new arrival".
 *
 * @author Frank Nimphius
 * @copyright Copyright (c) 2015 Oracle. All rights reserved.
 */
public class FiFPushHandler implements EventListener {
    public FiFPushHandler() {
        super();
        Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "FiFPushHandler::constructor",
                                       "Push activated");
    }


    public void onMessage(Event event) {

        String msg = event.getPayload();
        Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "FiFPushHandler::onMessage",
                                       "Raw push payload received from server = " + msg);

        // Parse the payload of the push notification
        HashMap payload = null;
        String pushMsg = "No message received";

        try {

            //todo payload must include incident report ID and a short message. This needs to be documented in Mireille's hands-on material
            payload = (HashMap) JSONBeanSerializationHelper.fromJSON(HashMap.class, msg);

//todo            
            /*
             * *** Design decision made by developer agreement. ***
             * 
             * Incident report Id must be written to                        #{applicationScope.push_incidentId} 
             * Push  message must be written to                             #{applicationScope.push_message}
             * Push  message title must be written to                       #{applicationScope.push_messageTitle}
             * Has new push message         true/false                      #{applicationScope.push_hasNewMessage}
             * 
             * see: Constants.java for Java handlers
             * 
             */
            
            
            pushMsg = (String) payload.get("alert");
            
            Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "FiFPushHandler::onMessage",
                                           "Push message alert = " + msg);


            //display notification message on screen
            ValueExpression ve =
                AdfmfJavaUtilities.getValueExpression("#{applicationScope.push.displayNotification}", Boolean.class);
            ve.setValue(AdfmfJavaUtilities.getAdfELContext(), Boolean.TRUE);

            //todo write information from the push message to an application scope variable. Application scope variables support property change events and thus update the screen


        }

        catch (Exception e) {
            e.printStackTrace();

        }
    }


    public void onError(AdfException adfException) {
        Utility.ApplicationLogger.logp(Level.WARNING, this.getClass().getSimpleName(), "FiFPushHandler::onError",
                                       "Message = " + adfException.getMessage() + "\nSeverity = " +
                                       adfException.getSeverity() + "\nType = " + adfException.getType());

        // Write the error into app scope
        ValueExpression ve =
            AdfmfJavaUtilities.getValueExpression("#{applicationScope.push.errorMessage}", String.class);
        ve.setValue(AdfmfJavaUtilities.getAdfELContext(), adfException.toString());

    }

    public void onOpen(String token) {

        Utility.ApplicationLogger.logp(Level.FINE, this.getClass().getSimpleName(), "FiFPushHandler::onOpen",
                                       "Registration token = " + token);
        // Clear error in app scope
        ValueExpression ve = AdfmfJavaUtilities.getValueExpression("#{applicationScope.errorMessage}", String.class);
        ve.setValue(AdfmfJavaUtilities.getAdfELContext(), null);

        // Write the token into app scope
        ve = AdfmfJavaUtilities.getValueExpression("#{applicationScope.deviceToken}", String.class);
        ve.setValue(AdfmfJavaUtilities.getAdfELContext(), token);

    }

}
