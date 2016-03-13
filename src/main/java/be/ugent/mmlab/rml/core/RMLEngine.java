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
    
    public void run(RMLMapping mapping, String outputFile, String outputFormat, 
            String graphName, Map<String,String> parameters, String[] exeTriplesMap,
            String metadataLevel, String metadataFormat, String metadataVocab);

    public RMLDataset runRMLMapping(RMLDataset dataset, RMLMapping rmlMapping,
            String baseIRI, Map<String, String> parameters, String[] exeTriplesMap);
    
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset);
    
    public RMLProcessor generateRMLProcessor(
            TriplesMap triplesMap, Map<String, String> parameters);
    
    public RMLDataset chooseSesameDataSet(String repositoryID,
            String pathToNativeStore, String outputFormat);
    
}
