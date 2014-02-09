package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.HashMap;
import java.util.List;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Performer to do joins with rr:joinCondition
 *
 * @author mielvandersande, andimou
 */
public class ConditionalJoinRMLPerformer extends NodeRMLPerformer{
    
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);
    private HashMap<String, String> conditions;
    private Resource subject;
    private URI predicate;

    public ConditionalJoinRMLPerformer(RMLProcessor processor, HashMap<String, String> conditions, Resource subject, URI predicate) {
        super(processor);
        this.conditions = conditions;
        this.subject = subject;
        this.predicate = predicate;
    }
    
    public ConditionalJoinRMLPerformer(RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }

    /**
     * Compare expressions from join to complete it
     * 
     * @param node current object in parent iteration
     * @param dataset
     * @param map 
     */
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        log.debug("[ConditionalJoinRMLPerformer:object] " + "node " + node.toString());
        Value object = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
        
        if (object == null){
            log.debug("[ConditionalJoinRMLPerformer:object] " + "No object found.");
            return;
        }        
        log.debug("[ConditionalJoinRMLPerformer:object] " + "Object " + object.toString());
        
        
        //iterate the conditions, execute the expressions and compare both values
        if(conditions != null){
            for (String expr : conditions.keySet()) {
                String cond = conditions.get(expr);
                
                log.debug("[ConditionalJoinRMLPerformer:condition] " + "Condition " + cond);

                List<String> values = processor.extractValueFromNode(node, expr);
                
                //MVS: Only allow one value with joins?
                //if a value doesn't match, stop right here
                if (cond == null || values.isEmpty() || !cond.equals(values.get(0)) ){
                    return;
                }
            }
        }
        log.debug("[ConditionalJoinRMLPerformer:addTriples] Subject "
                    + subject + " Predicate " + predicate + "Object " + object.toString());
        //add the join triple
        dataset.add(subject, predicate, object);
    }


}
