package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.HashMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Performer to do joins
 *
 * @author mielvandersande
 */
public class JoinRMLPerformer extends NodeRMLPerformer{
    
    private HashMap<String, String> conditions;
    private Resource subject;
    private URI predicate;

    public JoinRMLPerformer(RMLProcessor processor, HashMap<String, String> conditions, Resource subject, URI predicate) {
        super(processor);
        this.conditions = conditions;
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
        Value object = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
        
        if (object == null){
            return;
        }
        //iterate the conditions, execute the expressions and compare both values
        for (String expr : conditions.keySet()) {
            //if a value doesn't match, stop right here
            if (!conditions.get(expr).equals(processor.extractValueFromNode(node, expr))) {
                return;
            }
        }
        //add the join triple
        dataset.add(subject, predicate, object);
    }


}
