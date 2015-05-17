package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import java.io.InputStream;
import java.util.List;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;



/**
 * Interface for processing a certain term map
 * @author mielvandersande, andimou
 */
public interface RMLProcessor {
    
    /**
     * Iterate a list of nodes (objects, elements, rows) from the source and call the performer to handle the triplemap
     * @param dataset the ouput rdf dataset
     * @param map the triplemap
     * @param performer the performer handling the action done on the triplemap
     */
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer, InputStream input);
    
    public void execute_node(SesameDataSet dataset, String expression, TriplesMap parentTriplesMap, RMLPerformer performer, Object node, Resource subject);

    /**
     * Resolve an expression and extract a single string value from a node
     * @param node current object
     * @param expression reference to value
     * @return extracted value
     */
    public List<String> extractValueFromNode(Object node, String expression);
    /**
     * process a subject map
     * @param dataset
     * @param subjectMap
     * @param node
     * @return 
     */
    public Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node);
    
    public void processSubjectTypeMap(SesameDataSet dataset, Resource subject, SubjectMap subjectMap, Object node);
    
    /**
     *
     * @param map
     * @param replacements
     * @param expression
     * @return
     */
    public List<String> processTemplate(TermMap map, List<String> replacements, String expression);
    
    public String processTemplate(String expression, String template, String termType,
            RMLVocabulary.QLTerm referenceFormulation, String replacement);
    
    /**
     * process a predicate object map
     * @param dataset
     * @param subject the subject created by the subject map
     * @param pom the predicate object map
     * @param node 
     */
    public void processPredicateObjectMap(
            SesameDataSet dataset, Resource subject, 
            PredicateObjectMap pom, Object node, TriplesMap map);
    
    public List<URI> processPredicateMap(PredicateMap predicateMap, Object node);
    
    /**
     *
     * @param dataset
     * @param subject
     * @param predicate
     * @param pom
     * @param node
     */
    public void processPredicateObjectMap_ObjMap(
            SesameDataSet dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node);
    
    public List<Value> applyTermType(String value, List<Value> valueList, TermMap termMap);

    /**
     *
     * @param map
     * @param node
     * @return
     */
    public List<String> processTermMap(TermMap map, Object node);
    
    /**
     *
     * @param value
     * @return
     */
    public String cleansing(String value);
    
    /**
     *
     * @param split
     * @param node
     * @return
     */
    public List<String> postProcessLogicalSource(String split, Object node);
}
