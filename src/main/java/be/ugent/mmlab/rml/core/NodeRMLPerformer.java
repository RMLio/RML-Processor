/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;

/**
 *
 * @author mielvandersande
 */
public class NodeRMLPerformer implements RMLPerformer{
    
    protected RMLProcessor processor;

    public NodeRMLPerformer(RMLProcessor processor) {
        this.processor = processor;
    }

    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        Resource subject = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
        
        if (subject == null){
            return;
        }

        for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
            processor.processPredicateObjectMap(dataset, subject, pom, node);
        }
    }
    
}
