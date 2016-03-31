package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * This class contains all generic functionality for executing an iteration and
 * processing the mapping
 *
 * @author mielvandersande, andimou
 */
public abstract class AbstractRMLProcessor implements RMLProcessor {
    protected Integer enumerator = 0;
    protected TermMapProcessor termMapProcessor ;
    protected Map<String, String> parameters;
    protected MetadataGenerator metadataGenerator = null;

    /**
     * Gets the globally defined identifier-to-path map
     *
     * @param ls the current LogicalSource
     * @return the location of the file or table
     */
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(AbstractRMLProcessor.class.getSimpleName());

    /**
     * gets the expression specified in the logical source
     *
     * @param ls
     * @return
     */
    protected String getReference(LogicalSource ls) {
        return ls.getIterator();
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }

     
    @Override
    public Resource processSubjectMap(RMLProcessor processor, RMLDataset dataset, 
            TriplesMap map, SubjectMap subjectMap, Object node, String[] exeTriplesMap){
        SubjectMapProcessor subMapProcessor;
        
        if(!dataset.getClass().getSimpleName().equals("MetadataFileDataset")){
            subMapProcessor = new StdSubjectMapProcessor();
        }
        else{
            if(dataset.getMetadataLevel().equals("triplesmap") ||
                    dataset.getMetadataLevel().equals("triple") ||
                    dataset.getMetadataVocab().contains("co") ){
                
                subMapProcessor = new MetadataSubjectMapProcessor(metadataGenerator);
            }   
            else{
                subMapProcessor = new StdSubjectMapProcessor();
            }
        }
        Resource subject = subMapProcessor.processSubjectMap(
                dataset, subjectMap, node, processor);
        
        if (subject == null) {
            log.debug("No subject was generated for "
                    + map.getName() + " triple Map and node " + node.toString());
        } else {
            subMapProcessor.processSubjectTypeMap(dataset, subject, map, node);
        }
        return subject;
    }
    
    /**
     * Process a predicate object map
     *
     * @param dataset
     * @param subject   the subject from the triple
     * @param pom       the predicate object map
     * @param node      the current node
     */
    @Override
    public void processPredicateObjectMap(
            RMLDataset dataset, Resource subject, PredicateObjectMap pom, 
            Object node, TriplesMap map, String[] exeTriplesMap, RMLProcessor processor) {
        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            PredicateMapProcessor preMapProcessor = 
                    new PredicateMapProcessor(map, processor);
            //Get the predicates
            List<URI> predicates = 
                    preMapProcessor.processPredicateMap(predicateMap, node);
            
            if (predicates.size() > 0) {
                URI predicate = predicates.get(0);
                ObjectMapProcessor predicateObjectProcessor ;
                //        = new ObjectMapProcessor(map, processor);
                if(dataset.getMetadataLevel().equals("triple") ||
                        dataset.getMetadataVocab().contains("co")){
                    predicateObjectProcessor = 
                            new MetadataObjectMapProcessor(
                            map, processor,metadataGenerator);
                }
                else {
                    predicateObjectProcessor = 
                            new StdObjectMapProcessor(map, processor);
                }
                
                //Process the joins first
                predicateObjectProcessor.processPredicateObjectMap_RefObjMap(
                        dataset, subject, predicate, pom, node, 
                        map, parameters, exeTriplesMap);

                //process the objectmaps
                predicateObjectProcessor.processPredicateObjectMap_ObjMap(
                        dataset, subject, predicate, pom, node);
            }
            
        }
    }
    
    /**
     *
     * @param metadataGenerator
     */
    @Override
    public void setMetadataGenerator(MetadataGenerator metadataGenerator){
        this.metadataGenerator = metadataGenerator ;
    }
    
    /**
     *
     * @return
     */
    @Override
    public MetadataGenerator getMetadataGenerator(){
        return this.metadataGenerator ;
    }
    
    @Override
    public Integer getEnumerator(){
        return this.enumerator;
    }
}
