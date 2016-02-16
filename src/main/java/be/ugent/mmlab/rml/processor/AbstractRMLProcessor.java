package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import java.util.List;
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

    protected TermMapProcessor termMapProcessor ;

    /**
     * Gets the globally defined identifier-to-path map
     *
     * @param ls the current LogicalSource
     * @return the location of the file or table
     */
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(AbstractRMLProcessor.class);

    /**
     * gets the expression specified in the logical source
     *
     * @param ls
     * @return
     */
    protected String getReference(LogicalSource ls) {
        return ls.getIterator();
    }

     
    @Override
    public Resource processSubjectMap(RMLProcessor processor, RMLDataset originalDataset, 
            TriplesMap map, SubjectMap subjectMap, Object node, String[] exeTriplesMap){
        SubjectMapProcessor subMapProcessor;
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
                
        if(dataset.getClass().getSimpleName().equals("MetadataFileDataset")){
            log.debug("Generate metadata subjectmap processor");
            subMapProcessor = new MetadataSubjectMapProcessor();
        }
        else{
            if(dataset.getMetadataLevel().equals("triple"))
                subMapProcessor = new MetadataSubjectMapProcessor();
            else
                subMapProcessor = new StdSubjectMapProcessor();
        }
        Resource subject = subMapProcessor.processSubjectMap(
                dataset, subjectMap, node, processor);
        
        if (subject == null) {
            log.debug("No subject was generated for "
                    + map.getName() + "triple Map and node " + node.toString());
        } else {
            subMapProcessor.processSubjectTypeMap(
                    dataset, subject, map.getSubjectMap(), node);
            
            //Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
            if(map.getPredicateObjectMaps().size() > 0)
            for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                processor.processPredicateObjectMap(
                        dataset, subject, pom, node, map, exeTriplesMap, processor);
            }
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
            RMLDataset originalDataset, Resource subject, PredicateObjectMap pom, 
            Object node, TriplesMap map, String[] exeTriplesMap, RMLProcessor processor) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            PredicateMapProcessor preMapProcessor = 
                    new PredicateMapProcessor(map, processor);
            //Get the predicate
            List<URI> predicates = 
                    preMapProcessor.processPredicateMap(predicateMap, node);
            
            if (predicates.size() > 0) {
                URI predicate = predicates.get(0);
                StdObjectMapProcessor predicateObjectProcessor ;
                //        = new ObjectMapProcessor(map, processor);
                if(dataset.getMetadataLevel().equals("triple")){
                    predicateObjectProcessor = 
                            new MetadataObjectMapProcessor(map, processor);
                }
                else {
                    predicateObjectProcessor = 
                            new StdObjectMapProcessor(map, processor);
                }

                //Process the joins first
                predicateObjectProcessor.processPredicateObjectMap_RefObjMap(
                        dataset, subject, predicate, pom, node, map, exeTriplesMap);

                //process the objectmaps
                predicateObjectProcessor.processPredicateObjectMap_ObjMap(
                        dataset, subject, predicate, pom, node);
            }
            
        }
    }
}
