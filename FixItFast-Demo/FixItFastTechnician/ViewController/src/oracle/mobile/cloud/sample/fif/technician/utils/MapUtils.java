package oracle.mobile.cloud.sample.fif.technician.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author   Frank Nimphius
 * @coyright Oracle Corporation, 2015
 */
public class MapUtils {
    private MapUtils() {}
    
    /**
     * Flattens properties saved in a HashMap into an array representation of key/value pairs
     * 
     * @param properties
     * @return String of key/value pairs
     */
    public static String dumpProperties(Map<String,String> properties){
        Iterator keyIterator = properties.keySet().iterator();
        
        StringBuffer propertiesDump = new StringBuffer();
        propertiesDump.append("[");
        while(keyIterator.hasNext()){
            String key = (String) keyIterator.next();
            propertiesDump.append(key+":"+properties.get(key));
            if(keyIterator.hasNext()){
                propertiesDump.append(";");
            }
            else{
                propertiesDump.append("]");
            }
        }
        return  propertiesDump.toString();
    }
}
