package oracle.mobile.cloud.sample.fif.technician.mbeans.backing;

import oracle.adfmf.amx.event.ActionEvent;
import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;

import oracle.mobile.cloud.sample.fif.technician.app.log.AppLogger;
import oracle.mobile.cloud.sample.fif.technician.mbeans.util.ManagedBeansUtil;


/**
 * @author Frank Nimphius
 * @coyright Oracle Corporation, 2015
 */
public class SRListBacking {


    private static String DEFAULT_FILTER_CRITERIA = "All";
    private String incidentFilterCriteria = DEFAULT_FILTER_CRITERIA;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public SRListBacking() {
        super();
    }


    /**
     * Set filter value to query list items in incidet list. Allwed values are All, New, Open, Complete
     * @param incidentFilterList
     */
    public void setIncidentFilterCriteria(String incidentFilterList) {
        String oldIncidentFilterList = this.incidentFilterCriteria;
        this.incidentFilterCriteria = incidentFilterList;
        propertyChangeSupport.firePropertyChange("incidentFilterCriteria", oldIncidentFilterList, incidentFilterList);
    }

    public String getIncidentFilterCriteria() {
        return incidentFilterCriteria;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Filter the incidents list
     * @param action
     */
    public void invokeFilterList(ActionEvent action) {

        AppLogger.logFine("Invoke Data Control method \"filterIncidentListInMemory\" with argument value :" +
                          incidentFilterCriteria, this.getClass().getSimpleName(), "invokeFilterList");
        ManagedBeansUtil.invokeDCSingleStringParameterMethod("filterIncidentListInMemory", "filterValue", incidentFilterCriteria);
    }
    
    
    //called when reloading the incident list data
    public String resetFilterList(){
        //reset to default
        this.setIncidentFilterCriteria(DEFAULT_FILTER_CRITERIA);
        return null;
    }
}
