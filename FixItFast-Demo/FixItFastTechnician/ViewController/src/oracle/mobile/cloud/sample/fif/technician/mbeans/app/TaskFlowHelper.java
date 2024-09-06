package oracle.mobile.cloud.sample.fif.technician.mbeans.app;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;

import oracle.mobile.cloud.sample.fif.technician.mbeans.security.AuthenticationHandler;
import oracle.mobile.cloud.sample.fif.technician.mbeans.util.ManagedBeansUtil;

/**
 *
 * Utility class that supports communication between pages and message on a page
 *
 * @author Frank Nimphius
 * @copyright Copyright (c) 2015 Oracle. All rights reserved.
 */
public class TaskFlowHelper {
    
    //temporarily holds id of incident to show details for
    private String _incidentId = null;
        
   //message to display on page
    private String displayMessage = null;
    
    public TaskFlowHelper() {
        super();
    }


    /*
     * *** SETTER / GETTER METHODS
     */
    
    //the x-week schema custom API does not proide information about the driving time to 
    //the location for the single incident query. As a result, distance to location is 
    //always unknown for the SRDetail page. To solve this problem in the app, we copy 
    //the information to this managed bean bean upon incident selection
    String drivingTime = null;


    //save the distance to localtion for the current view
    public void setDrivingTime(String distanceToLocation) {
        String oldDistanceToLocation = this.drivingTime;
        this.drivingTime = distanceToLocation;
        propertyChangeSupport.firePropertyChange("drivingTime", oldDistanceToLocation, distanceToLocation);
    }

    public String getDrivingTime() {
        return drivingTime;
    }

    /**
     * Incident Id of report that should be shown on a detail page. This method is called from the 
     * list view page after user selection on an incident report or by a method activity if the 
     * incident report ID is delivered via Push
     * 
     * @param incidentId String identifying the Id of a reported incident
     */
    public void setIncidentId(String incidentId) {
        String oldIncidentId = this._incidentId;
        this._incidentId = incidentId;
        propertyChangeSupport.firePropertyChange("IncidentId", oldIncidentId, _incidentId);
        
        //next, update the data control with the change      
        ManagedBeansUtil.invokeDCSingleStringParameterMethod("setCurrentIncidentId", "currentIncidentId", incidentId);        
    }

    public String getIncidentId() {
        return _incidentId;
    }


    public void setDisplayMessage(String _errorMessageToDisplay) {
        String oldErrorMessageToDisplay = this.displayMessage;
        this.displayMessage = _errorMessageToDisplay;
        propertyChangeSupport.firePropertyChange("ErrorMessageToDisplay", oldErrorMessageToDisplay,
                                                 _errorMessageToDisplay);
    }

    /**
     * Method is referenced from error message elements on the views
     * @return Error message
     */
    public String getDisplayMessage() {
        return displayMessage;
    }
    
    
    

    /* 
     * **** PROPERTY CHANGE SUPPORT ****
     */
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /* TASKFLOW ANALYTIC HELPER METHODS */
    
    
    public void addPostAuthenticationValidationAnalyticEvent(){
        HashMap<String,String> customEventDef = new  HashMap<String,String>();
        
        AuthenticationHandler authenticationHandler = (AuthenticationHandler) AdfmfJavaUtilities.evaluateELExpression("#{pageFlowScope.authenticationHandler}");
        
        customEventDef.put("username", authenticationHandler.getUsername());
        customEventDef.put("action","Query Incidents"); 
        customEventDef.put("source","task flow navigation"); 
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date();
        String timeStamp = sdf.format(d);
        
        customEventDef.put("timeStamp",timeStamp);            
        ManagedBeansUtil.addCustomAnalyticEvent("post user validation",customEventDef);
    }
    
    public void addNavigationAnalyticEvent(String toView){
        HashMap<String,String> customEventDef = new  HashMap<String,String>();
        
        AuthenticationHandler authenticationHandler = (AuthenticationHandler) AdfmfJavaUtilities.evaluateELExpression("#{pageFlowScope.authenticationHandler}");
        
        customEventDef.put("username", authenticationHandler.getUsername());
        customEventDef.put("action","task flow navigation"); 
        customEventDef.put("target",toView); 
        customEventDef.put("source","task flow navigation"); 
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date();
        String timeStamp = sdf.format(d);
        
        customEventDef.put("timeStamp",timeStamp);            
        ManagedBeansUtil.addCustomAnalyticEvent("view-navigation",customEventDef);
    }
    
    public void addLoginAnalyticEvent(String loginStatus){
        HashMap<String,String> customEventDef = new  HashMap<String,String>();
        
        AuthenticationHandler authenticationHandler = (AuthenticationHandler) AdfmfJavaUtilities.evaluateELExpression("#{pageFlowScope.authenticationHandler}");
        
        customEventDef.put("username", authenticationHandler.getUsername());
        customEventDef.put("status","loginStatus"); 
        customEventDef.put("action","application login"); 
        customEventDef.put("source","task flow navigation"); 
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date();
        String timeStamp = sdf.format(d);
        
        customEventDef.put("timeStamp",timeStamp);            
        ManagedBeansUtil.addCustomAnalyticEvent("view-navigation",customEventDef);
    }
    
    public void addLogoutAnalyticEvent(){
        HashMap<String,String> customEventDef = new  HashMap<String,String>();
        
        AuthenticationHandler authenticationHandler = (AuthenticationHandler) AdfmfJavaUtilities.evaluateELExpression("#{pageFlowScope.authenticationHandler}");
        
        customEventDef.put("username", authenticationHandler.getUsername());
        customEventDef.put("action","application logout"); 
        customEventDef.put("source","task flow navigation"); 
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date();
        String timeStamp = sdf.format(d);
        
        customEventDef.put("timeStamp",timeStamp);            
        ManagedBeansUtil.addCustomAnalyticEvent("view-navigation",customEventDef);
    }
    
    public void addPushNavigationAnalyticEvent(String toView){
        HashMap<String,String> customEventDef = new  HashMap<String,String>();
        
        AuthenticationHandler authenticationHandler = (AuthenticationHandler) AdfmfJavaUtilities.evaluateELExpression("#{pageFlowScope.authenticationHandler}");
        
        customEventDef.put("username", authenticationHandler.getUsername());
        customEventDef.put("action","SRDetail"); 
        customEventDef.put("source","push"); 
        customEventDef.put("target",toView); 
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date();
        String timeStamp = sdf.format(d);
        
        customEventDef.put("timeStamp",timeStamp);            
        ManagedBeansUtil.addCustomAnalyticEvent("view-navigation",customEventDef);
    }
    
    //end analytic session and report event queue to MCS server
    public void flushAnalyticEventsToServer(){
        //issue data control method call with no arguments
        ManagedBeansUtil.invokeOnDataControl("flushAnalyticEventsToServer", new ArrayList<String>(),
                                                   new ArrayList<Object>(), new ArrayList<Class>());
    }
    
}
