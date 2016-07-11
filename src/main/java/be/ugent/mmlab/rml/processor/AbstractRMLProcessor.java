package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.extraction.TermExtractor;
import be.ugent.mmlab.rml.model.RDFTerm.*;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.std.StdConditionPredicateObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import be.ugent.mmlab.rml.vocabularies.FnVocabulary;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
    protected boolean iterationStatus = false;

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
            Object node, TriplesMap map, String[] exeTriplesMap,
            RMLProcessor processor, GraphMap graphMap) {
        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        boolean flag = true;
        //TODO: create processConditionPredicateObjectMap instead
        if (pom.getClass().getSimpleName().equals("StdConditionPredicateObjectMap")) {
            log.debug("Processing conditional POM");
            
            StdConditionPredicateObjectMap tmp =
                    (StdConditionPredicateObjectMap) pom;
            Set<Condition> conditions = tmp.getConditions();

//            TermMapProcessorFactory factory = new ConcreteTermMapFactory();
//            this.termMapProcessor = factory.create(
//                map.getLogicalSource().getReferenceFormulation(), processor);

            //process conditions
            ConditionProcessor condProcessor = new StdConditionProcessor();
            if(conditions.size() > 0){
                flag = condProcessor.processConditions(
                    node, termMapProcessor, conditions);
            }
            if (!flag) {
                //Takes the first conditions
                //TODO: Change it to get more conditions
                StdConditionPredicateObjectMap pom2 = 
                        (StdConditionPredicateObjectMap) pom;
                Set<PredicateObjectMap> fallbacks = pom2.getFallbackPOMs();

                for (PredicateObjectMap fallback : fallbacks) {
                    //TODO: Calculate fallbackGraphMap
                    GraphMap fallbackGraphMap = null;
                        processor.processPredicateObjectMap(dataset, subject, fallback,
                                node, map, exeTriplesMap, processor, fallbackGraphMap);
                }
            }
            else
                log.debug("No conditions found.");
        }
        //TODO: Till here
        if (flag) {
            log.debug("Proceed with the POM");
            
            //Go over each predicate map
            for (PredicateMap predicateMap : predicateMaps) {
                PredicateMapProcessor preMapProcessor =
                        new PredicateMapProcessor(map, processor);
                //Get the predicates
                List<URI> predicates =
                        preMapProcessor.processPredicateMap(predicateMap, node);
                if(graphMap == null){
                    graphMap = predicateMap.getGraphMap();
                }

                if (predicates.size() > 0) {
                    URI predicate = predicates.get(0);
                    ObjectMapProcessor predicateObjectProcessor;
                    if (dataset.getMetadataLevel().equals("triple")
                            || dataset.getMetadataVocab().contains("co")) {
                        predicateObjectProcessor =
                                new MetadataObjectMapProcessor(
                                map, processor, metadataGenerator);
                    } else {
                        predicateObjectProcessor =
                                new StdObjectMapProcessor(map, processor);
                    }

                    //Process the joins first
                    Set<ReferencingObjectMap> referencingObjectMaps =
                            pom.getReferencingObjectMaps();
                    if(referencingObjectMaps != null && referencingObjectMaps.size() > 0)
                        predicateObjectProcessor.processPredicateObjectMap_RefObjMap(
                                dataset, subject, predicate, referencingObjectMaps, node,
                                map, parameters, exeTriplesMap, graphMap);

                    //Process the Function Object Maps
                    Set<FunctionTermMap> functionTermMaps =
                            pom.getFunctionTermMaps();
                    if(functionTermMaps != null && functionTermMaps.size() > 0)
                        predicateObjectProcessor.processPredicateObjectMap_FunMap(
                                dataset, subject, predicate, functionTermMaps, node, map, exeTriplesMap, graphMap);

                    //process the Object Maps
                    predicateObjectProcessor.processPredicateObjectMap_ObjMap(
                            dataset, subject, predicate, pom, node, graphMap);


                }

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
    
    public void setIterationStatus(boolean status){
        this.iterationStatus = status;
    }
    
    @Override
    public boolean getIterationStatus(){
        return this.iterationStatus;
    }
}
