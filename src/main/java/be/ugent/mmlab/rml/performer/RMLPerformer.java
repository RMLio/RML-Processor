package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.Map;
import org.openrdf.model.Resource;

/**
 * RML Processor
 *
 * Interface for executing context-dependent operations like a regular object map, or a join
 * 
 * @author mielvandersande, andimou
 */
public interface RMLPerformer {
    /**
     * Perform the action
     * 
     * @param node current object in the iteration
     * @param dataset dataset for endresult
     * @param map current triple map that is being processed
     */
    public void perform(Object node, RMLDataset dataset, TriplesMap map, 
            String[] exeTriplesMap, Map<String, String> parameters, 
            boolean pomExecution);
    
    /**
     *
     * @param node
     * @param dataset
     * @param map
     * @param subject
     * @param exeTriplesMap
     */
    public void perform(Object node, RMLDataset dataset, 
            TriplesMap map, Resource subject, String[] exeTriplesMap);
}
