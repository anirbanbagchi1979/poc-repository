package oracle.mobile.cloud.sample.fif.technician.utils;

import oracle.adf.model.datacontrols.device.DeviceManager;
import oracle.adf.model.datacontrols.device.DeviceManagerFactory;

import oracle.adfmf.framework.ApplicationInformation;
import oracle.adfmf.framework.api.AdfmfContainerUtilities;


/**
 * Abstraction layer to the MAF APIs
 *
 * @author   Frank Nimphius
 * @coyright Oracle Corporation, 2015
 */
public class MAFUtil {
    private static final String NETWORK_STATUS_NONE = "none";
    
    public MAFUtil() {
        super();
    }
    
    /**
     * Check if network access is available. 
     * @return true if there is WIFI or 3,4 GM network
     */
    public static boolean isNetworkAccess(){
                               
        boolean isNetwork = false;
        isNetwork = !DeviceManagerFactory.getDeviceManager().getNetworkStatus().equalsIgnoreCase(NETWORK_STATUS_NONE);
        return isNetwork;
    }
    
    /**
     * Checks if the device supports GEO location retireval, which could be disabled by the device not supporting 
     * GEO locations or the application missing the required permission
     * 
     * @return true if GEO location data can be accessed. False otherwise
     */
    public static boolean isGeoLocationAvailable(){
        return DeviceManagerFactory.getDeviceManager().hasGeolocation();
    }
    
    /**
     *
     * Determibes manufacturer based on OS
     * @return Apple for iOS, Google for Android, Other for the rest
     */
    public static String getDeviceManufacturer(){
        //Todo look for Cordova Plugin to get detailed information
        ApplicationInformation appInformation = AdfmfContainerUtilities.getApplicationInformation();
        DeviceManager deviceManager = DeviceManagerFactory.getDeviceManager();
        String _manufacturerOS = deviceManager.getOs().toUpperCase();
        
        String manufacturer = _manufacturerOS.equalsIgnoreCase("IOS") ? "Apple" :
                              _manufacturerOS.contains("ANDROID") ? "Google" : "OTHER";      
        return manufacturer;
        
    }
    
    /**
     * Detect the device operation system
     * @return
     */
    public static String getDeviceOS(){
       
        ApplicationInformation appInformation = AdfmfContainerUtilities.getApplicationInformation();
        DeviceManager deviceManager = DeviceManagerFactory.getDeviceManager();
        return deviceManager.getOs();
        
    }
    
    public static String getDeviceOSVersion(){
       
        ApplicationInformation appInformation = AdfmfContainerUtilities.getApplicationInformation();
        DeviceManager deviceManager = DeviceManagerFactory.getDeviceManager();
        return deviceManager.getVersion();        
    }
    
    
}
