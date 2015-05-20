package be.ugent.mmlab.rml.input.std;

import be.ugent.mmlab.rml.input.model.InputSource;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdInputSource implements InputSource{
    
    // Log
    private static final Logger log = LogManager.getLogger(StdInputSource.class);
    
    private String name;
    private String source;
    private Set<TriplesMap> triplesMaps;

    public StdInputSource(String name) {
        setName(name);
    }

    public StdInputSource(String name, String source) {
        setSource(source);
        setName(name);
    }

    /**
     *
     * @param name
     */
    private void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }
    
    public String getName(){
        return this.name;
    }
    
    private void setSource(String source) {
        if (source != null) {
            this.source = source;
        }
    }
    
    @Override
    public String getSource(){
        return this.source;
    }

    /**
     *
     * @param triplesMap
     */
    @Override
    public void addTriplesMap(TriplesMap triplesMap) {
        if (triplesMap != null) {
            triplesMaps.add(triplesMap);
        }
    }

    private void setTriplesMap(String triplesMap) {
        this.triplesMaps = new HashSet<TriplesMap>();
    }

}
