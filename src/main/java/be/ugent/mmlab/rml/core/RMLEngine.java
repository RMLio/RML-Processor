package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.Map;

/**
 *
 * @author andimou
 */
public interface RMLEngine {
    
    public RMLDataset runRMLMapping(
    RMLMapping rmlMapping, String baseIRI, 
            Map<String, String> parameters, String[] triplesMap);

    public RMLDataset runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore, String outputFormat, 
            Map<String, String> parameters, String[] exeTriplesMap, String prov);
    
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset);
    
    public RMLProcessor generateRMLProcessor(TriplesMap triplesMap);
    
}
