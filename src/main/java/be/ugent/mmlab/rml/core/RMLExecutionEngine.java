package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * RML Processor
 *
 * @author andimou
 */
public class RMLExecutionEngine {
    String[] exeTriplesMap;
    
    public RMLExecutionEngine(String[] exeTriplesMap){
        this.exeTriplesMap = exeTriplesMap;
    }
    
    public boolean checkExecutionList(TriplesMap triplesMap, String[] exeTriplesMap) {
        boolean flag = false;

        for (String exeTM : exeTriplesMap) {
            flag = false;

            if (triplesMap.getName().toString().equals(exeTM.toString())) {
                flag = true;
            }
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

}
