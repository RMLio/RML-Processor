package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import org.eclipse.rdf4j.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class NestedRMLPerformer extends NodeRMLPerformer {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(NestedRMLPerformer.class);
    
    public NestedRMLPerformer(RMLProcessor processor) {
        super(processor);
    }
    
    public boolean perform(Object node, RMLDataset dataset, Resource refObjSub,
            TriplesMap map, String[] exeTriplesMap, boolean pomExecution) {
        boolean result = false;
        //TODO: Check how to handle this graph Map
        GraphMap graphMap = null;
        if (pomExecution || map.getSubjectMap().getTermType().equals(TermType.BLANK_NODE)) {
            log.debug("Executing entirely the Referencing Object Map.");
            //Resource refObjSub = processor.processSubjectMap(
            //        dataset, map.getSubjectMap(), node);
            //processor.processSubjectTypeMap(
            //        dataset, refObjSub, map.getSubjectMap(), node);
            for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                processor.processPredicateObjectMap(
                        dataset, refObjSub, pom, node, map, exeTriplesMap, null, graphMap);
            }
        }
        return result;
    }
    
}
