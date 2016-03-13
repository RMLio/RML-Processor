package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.InputStream;
import org.openrdf.model.Resource;

/**
 * RMLProcessor 
 * 
 * Interface for processing a certain term map
 * 
 * @author mielvandersande, andimou
 */
public interface RMLProcessor {
    
    /**
     * Iterate a list of nodes (objects, elements, rows) from the source and call the performer to handle the triplemap
     * @param dataset the ouput rdf dataset
     * @param map the triplemap
     * @param performer the performer handling the action done on the triplemap
     */
    public void execute(RMLDataset dataset, TriplesMap map, 
            RMLPerformer performer, InputStream input,
            String[] exeTriplesMap, boolean pomExecution);
    
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution);

    /**
     * process a subject map
     * @param dataset
     * @param subjectMap
     * @param node
     * @return 
     */
    public Resource processSubjectMap(RMLProcessor processor, RMLDataset dataset, 
            TriplesMap map, SubjectMap subjectMap, Object node, String[] exeTriplesMaps);
    
    /**
     * process a predicate object map
     * @param dataset
     * @param subject the subject created by the subject map
     * @param pom the predicate object map
     * @param node 
     */
    public void processPredicateObjectMap(
            RMLDataset dataset, Resource subject, PredicateObjectMap pom, 
            Object node, TriplesMap map, String[] exeTriplesMap, RMLProcessor processor);
   
    public MetadataGenerator getMetadataGenerator();
    
    public void setMetadataGenerator(MetadataGenerator metadataGenerator);
}
