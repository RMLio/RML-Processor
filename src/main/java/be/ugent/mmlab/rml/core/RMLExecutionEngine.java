package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class RMLExecutionEngine {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(RMLExecutionEngine.class);
    
    String[] exeTriplesMap;
    
    public RMLExecutionEngine(String[] exeTriplesMap){
        this.exeTriplesMap = exeTriplesMap;
    }
    
    public boolean checkExecutionList(
            TriplesMap triplesMap, String[] exeTriplesMap) {
        boolean flag = false;

        for (String exeTM : exeTriplesMap) {
            flag = false;

            if (triplesMap.getName().toString().equals(exeTM.toString())) {
                flag = true;
            }
        }

        return flag;
    }
    
    public boolean checkExecutionList(
            TriplesMap triplesMap, String exeTriplesMap) {
        boolean flag = false;

        if (triplesMap.getName().toString().equals(exeTriplesMap.toString())) {
            flag = true;
        }
        return flag;
    }
    
    public Collection<TriplesMap> processExecutionList(
            RMLMapping rmlMapping, String[] exeTriplesMap) {
        Collection<TriplesMap> tms = rmlMapping.getTriplesMaps();
        Collection<TriplesMap> triplesMaps = new LinkedHashSet<TriplesMap>();

        for (String exeTM : exeTriplesMap) {
            for (TriplesMap tm : tms) {
                if (tm.getName().equals(exeTM.toString())) {
                    triplesMaps.add(tm);
                }
            }
        }

        return triplesMaps;
    }
    
    public Collection<TriplesMap> registerLogicalSources(RMLMapping rmlMapping) {
        Collection<TriplesMap> tms = rmlMapping.getTriplesMaps();
        Collection<TriplesMap> triplesMaps = new LinkedHashSet<TriplesMap>();

        for (TriplesMap tm : tms) {
            tm.getLogicalSource().getSource();
        }

        return triplesMaps;
    }

}
