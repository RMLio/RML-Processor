package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;



/**
 * Interface for processing a certain term map
 * @author mielvandersande
 */
public interface RMLProcessor {
    
    /**
     * Iterate a list of nodes (objects, elements, rows) from the source and call the performer to handle the triplemap
     * @param dataset the ouput rdf dataset
     * @param map the triplemap
     * @param performer the performer handling the action done on the triplemap
     */
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer);
    /**
     * Resolve an expression and extract a single string value from a node
     * @param node current object
     * @param expression reference to value
     * @return extracted value
     */
    public String extractValueFromNode(Object node, String expression);
    /**
     * process a subject map
     * @param dataset
     * @param subjectMap
     * @param node
     * @return 
     */
    public Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node);
    /**
     * process a predicate object map
     * @param dataset
     * @param subject the subject created by the subject map
     * @param pom the predicate object map
     * @param node 
     */
    public void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node);
}
