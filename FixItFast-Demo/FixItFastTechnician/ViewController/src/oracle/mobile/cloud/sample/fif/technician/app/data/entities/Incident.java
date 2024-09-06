package oracle.mobile.cloud.sample.fif.technician.app.data.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;
import oracle.adfmf.json.JSONException;
import oracle.adfmf.json.JSONObject;

import oracle.mobile.cloud.sample.fif.technician.app.log.AppLogger;


/**
 *
 * Entity representing FIF allIncidents
 *
 * Example payload of an incident is shown below
 *
 * {
 * "id": 61,
 * "title": "Leaking Water Heater",
 * "createdon": "2015-03-27 14:12:13 UTC",
 * "status": "New",
 * "priority": "Low",
 * "imageLink": "/mobile/platform/storage/collections/FIF_UserData/objects/db89fd14-7ab0-4fe1-8593-5fffd8fdf974?user=46c5692a-bbed-4417-80d2-f114e41dc32a",
 * "notes": "\n2015-03-27T14:12:13.472Z\nAOSmith water heater leaking from right valve. Please hurry- water all over basement floor.\n\n2015-03-27T14:13:01.707Z\nMore water now.\n\n",
 * "technician": "joe@fixit.com",
 * "contact": {
 * "name": "Lynn Smith",
 * "street": "45 O Connor Street",
 * "city": "Ottawa",
 * "postalcode": "12345",
 * "username": "lynn"
 * }
 * }
 *
 * @author Frank Nimphius
 * @coyright Oracle Corporation, 2015
 *
 */
public class Incident {

    private Date createdOn = null;
    private String customerName = null;
    private String drivingTime = null;
    private Integer id = null;
    private String street = null;
    private String city = null;
    private List<String> notes = null;
    private String priority = "Low";
    
    private String priorityImg = "pri1.png";
    private String remoteImgLink = null;
    private String status = null;
    private String title = null;
    private String postalCode = null;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Incident() {
        super();
    }

    public Incident(int id, String title, String customerName, Date createdOn, String priority, String status,
                    String drivingTime, List<String> notes, String city, String street, String postalCode, String imLink) {
        this.id = id;
        this.title = title;
        this.customerName = customerName;
        this.createdOn = createdOn;
        this.priority = priority;
        this.status = status;
        this.drivingTime = drivingTime;
        this.notes = notes;
        this.city = city;
        this.street = street;
        this.remoteImgLink = imLink;
        this.postalCode = postalCode;
    }


    public void setCreatedOn(Date createdOn) {
        Date oldCreatedOn = this.createdOn;
        this.createdOn = createdOn;
        propertyChangeSupport.firePropertyChange("createdOn", oldCreatedOn, createdOn);
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCustomerName(String customerName) {
        String oldCustomerName = this.customerName;
        this.customerName = customerName;
        propertyChangeSupport.firePropertyChange("customerName", oldCustomerName, customerName);
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setDrivingTime(String drivingTime) {
        String oldDrivingTime = this.drivingTime;
        this.drivingTime = drivingTime;
        propertyChangeSupport.firePropertyChange("drivingTime", oldDrivingTime, drivingTime);
    }

    public String getDrivingTime() {
        return drivingTime;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        propertyChangeSupport.firePropertyChange("id", oldId, id);
    }

    public Integer getId() {
        return id;
    }

    

    public void setNotes(List<String> notes) {
        List<String> oldNotes = this.notes;
        this.notes = notes;
        propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setPriority(String priority) {
        String oldPriority = this.priority;
        this.priority = priority;
        propertyChangeSupport.firePropertyChange("priority", oldPriority, priority);
    }

    public String getPriority() {
        return priority;
    }

    public void setRemoteImgLink(String remoteImgLink) {
        String oldRemoteImgLink = this.remoteImgLink;
        this.remoteImgLink = remoteImgLink;
        propertyChangeSupport.firePropertyChange("remoteImgLink", oldRemoteImgLink, remoteImgLink);
    }

    public String getRemoteImgLink() {
        return remoteImgLink;
    }

    public void setStatus(String status) {
        String oldStatus = this.status;
        this.status = status;
        propertyChangeSupport.firePropertyChange("status", oldStatus, status);
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertyChangeSupport.firePropertyChange("title", oldTitle, title);
    }

    public String getTitle() {
        return title;
    }


    public void setPriorityImg(String priorityImg) {
        String oldPriorityImg = this.priorityImg;
        this.priorityImg = priorityImg;
        propertyChangeSupport.firePropertyChange("priorityImg", oldPriorityImg, priorityImg);
    }

    public String getPriorityImg() {
        return priorityImg;
    }


    public void setStreet(String street) {
        String oldStreet = this.street;
        this.street = street;
        propertyChangeSupport.firePropertyChange("street", oldStreet, street);
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String city) {
        String oldCity = this.city;
        this.city = city;
        propertyChangeSupport.firePropertyChange("city", oldCity, city);
    }

    public String getCity() {
        return city;
    }

    public void setPostalCode(String postalCode) {
        String oldPostalCode = this.postalCode;
        this.postalCode = postalCode;
        propertyChangeSupport.firePropertyChange("postalCode", oldPostalCode, postalCode);
    }

    public String getPostalCode() {
        return postalCode;
    }


    /**
     * Extracts the entity attributes from the JSON object argument
     * @param incidentJSONObject expects the JSONObject to represent a single FIF incident
     * @throws JSONException indicating that the entity could not be populated from information in the JSONObject
     */
    public void populateInstanceFromJSON(JSONObject incidentJSONObject) throws JSONException {
        try {
            this.title = incidentJSONObject.getString("title");
            this.id = incidentJSONObject.getInt("id");
            
            this.customerName = incidentJSONObject.getJSONObject("contact").getString("name");
                        
            this.createdOn = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(incidentJSONObject.getString("createdon"));
            this.priority = incidentJSONObject.getString("priority");                           
            this.status = incidentJSONObject.getString("status");
            
            
            //set icon
            if(this.getPriority().equalsIgnoreCase("HIGH") && !this.getStatus().equalsIgnoreCase("COMPLETE")){
                this.setPriorityImg("pri1.png");
            }
            else if(this.getPriority().equalsIgnoreCase("MEDIUM")&& !this.getStatus().equalsIgnoreCase("COMPLETE")){
                this.setPriorityImg("pri2.png");
            }
            else if(this.getPriority().equalsIgnoreCase("LOW")&& !this.getStatus().equalsIgnoreCase("COMPLETE")){
                this.setPriorityImg("pri3.png");
            }
            //show no priority
            else{
                this.setPriorityImg("pri4.png");
            }
            
            this.drivingTime = incidentJSONObject.optString("driveTime", "Unknown");
            
            this.remoteImgLink = incidentJSONObject.optString("imageLink", "");
            
            
            //a name contct is always provided. Other information may be optional or missing
            //so we need to read them so we can opt-in a default value (empty)
            JSONObject contactInfo = incidentJSONObject.getJSONObject("contact");
            this.street = contactInfo.optString("street", "");            
            this.city = contactInfo.optString("city", "");            
            this.postalCode = contactInfo.optString("postalCode", "");
               
            String note = incidentJSONObject.optString("notes","");
            
            if (note != null && note.length() != 0) {
                //the note string is delimited by a line break. This code tries to 
                String[] arrayOfNotes = note.split("\\r?\\n");
                List<String> listOfNotes = new ArrayList<String>(Arrays.asList(arrayOfNotes));
                //remove all entries that are empty or only have a single empty character
                listOfNotes.removeAll(Arrays.asList("", " "));
                notes = Arrays.asList(arrayOfNotes);
            }
        } catch (ParseException jse) {
            //could not parse the note information. 
            AppLogger.logWarning("Could not parse: "+this.id+" and date string: "+incidentJSONObject.getString("createdon"), 
                                 this.getClass().getSimpleName(), "populateInstanceFromJSON");
            
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
}
