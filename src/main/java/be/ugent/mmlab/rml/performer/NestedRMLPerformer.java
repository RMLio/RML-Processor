package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import org.openrdf.model.Resource;
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
    
    @Override
    public void perform(Object node, RMLDataset dataset, 
            TriplesMap map, String[] exeTriplesMap, boolean pomExecution) {
        if (pomExecution) {
            log.debug("Executing entirely the Referencing Object Map.");
            Resource refObjSub = processor.processSubjectMap(
                    dataset, map.getSubjectMap(), node);
            processor.processSubjectTypeMap(
                    dataset, refObjSub, map.getSubjectMap(), node);
            for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                processor.processPredicateObjectMap(
                        dataset, refObjSub, pom, node, map, exeTriplesMap);
            }
        }
    }

}
