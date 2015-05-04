package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.List;
import java.util.Set;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Performs the normal handling of an object in the iteration.
 * 
 * @author mielvandersande, andimou
 * 
 */
public class NodeRMLPerformer implements RMLPerformer{
    
    private static Log log = LogFactory.getLog(NodeRMLPerformer.class);
    
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
    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        if (map.getLogicalSource().getSplitCondition() == null) {
            Resource subject = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
            processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);
            if (subject == null) {
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[NodeRMLPerformer:perform] No subject was generated for " 
                        + map.getName() + "triple Map and row " + node.toString());
            } else {
                Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
                for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                    processor.processPredicateObjectMap(dataset, subject, pom, node, map);
                }
            }
        } else {
            perform(node, dataset, map, map.getLogicalSource().getSplitCondition());
        }
    }
    
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map, String splitCondition) {
        List<String> list = processor.postProcessLogicalSource(splitCondition, node);
        for (String item : list) {
            node = item;
            Resource subject = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
            processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);
            if (subject == null) {
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[NodeRMLPerformer:perform] No subject was generated for " 
                        + map.getName() + "triple Map and row " + node.toString());
            } else {
                Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
                for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                    processor.processPredicateObjectMap(dataset, subject, pom, node, map);
                }
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
    public void perform(Object node, SesameDataSet dataset, TriplesMap map, Resource subject) {
        processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);
        for (PredicateObjectMap pom : map.getPredicateObjectMaps()) 
            processor.processPredicateObjectMap(dataset, subject, pom, node, map);
    }
    }
