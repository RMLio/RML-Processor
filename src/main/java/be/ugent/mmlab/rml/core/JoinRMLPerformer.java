/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.HashMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
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

    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        Value object = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
        
        if (object == null){
            return;
        }

        for (String expr : conditions.keySet()) {
            if (!conditions.get(expr).equals(processor.extractValueFromNode(node, expr))) {
                return;
            }
        }
        dataset.add(subject, predicate, object);
    }


}
