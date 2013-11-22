/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;



/**
 *
 * @author mielvandersande
 */
public interface RMLProcessor {
    
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer);
    public String extractValueFromNode(Object node, String expression);
    public Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node);
    public void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node);
}
