package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;

/**
 * RML Processor
 * 
 * Performs the normal handling of an object in the iteration.
 * 
 * @author mielvandersande, andimou
 * 
 */
public class NodeRMLPerformer implements RMLPerformer {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(NodeRMLPerformer.class);
    protected RMLProcessor processor;

    /**
     *
     * @param processor the instance processing these nodes
     */
    public NodeRMLPerformer(RMLProcessor processor) {
        this.processor = processor;
    }

    /**
     * Process the subject map and predicate-object maps
     *
     * @param node current object in the iteration
     * @param dataset dataset for endresult
     * @param map current triple map that is being processed
     */
    @Override
    public void perform(Object node, RMLDataset dataset, 
    TriplesMap map, String[] exeTriplesMap, boolean pomExecution) {

        Resource subject = 
                processor.processSubjectMap(dataset, map.getSubjectMap(), node);

        if (subject == null) {
            log.debug("No subject was generated for "
                    + map.getName() + "triple Map and node " + node.toString());
        } else {
            processor.processSubjectTypeMap(
                    dataset, subject, map.getSubjectMap(), node);
            
            Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
            for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                processor.processPredicateObjectMap(
                        dataset, subject, pom, node, map, exeTriplesMap);
            }
        }
    }

    /**
     *
     * @param node
     * @param dataset
     * @param map
     * @param subject
     */
    @Override
    public void perform(Object node, RMLDataset dataset, TriplesMap map, 
        Resource subject, String[] exeTriplesMap) {
        processor.processSubjectTypeMap(
                dataset, subject, map.getSubjectMap(), node);
        for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
            processor.processPredicateObjectMap(
                    dataset, subject, pom, node, map, exeTriplesMap);
        }
    }
}
