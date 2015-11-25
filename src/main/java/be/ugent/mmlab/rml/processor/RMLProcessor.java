package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import java.io.InputStream;
import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;

/**
 * RMLProcessor 
 * 
 * Interface for processing a certain term map
 * 
 * @author mielvandersande, andimou
 */
public interface RMLProcessor {
    
    /**
     * Iterate a list of nodes (objects, elements, rows) from the source and call the performer to handle the triplemap
     * @param dataset the ouput rdf dataset
     * @param map the triplemap
     * @param performer the performer handling the action done on the triplemap
     */
    public void execute(RMLDataset dataset, TriplesMap map, 
            RMLPerformer performer, InputStream input, 
            String[] exeTriplesMap, boolean pomExecution);
    
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution);

    /**
     * process a subject map
     * @param dataset
     * @param subjectMap
     * @param node
     * @return 
     */
    public Resource processSubjectMap(RMLProcessor processor, RMLDataset dataset, 
            TriplesMap map, SubjectMap subjectMap, Object node, String[] exeTriplesMaps);
    
    /**
     *
     * @param dataset
     * @param subject
     * @param subjectMap
     * @param node
     */
    //public void processSubjectTypeMap(RMLDataset dataset, Resource subject, 
    //        SubjectMap subjectMap, Object node);
    
    /**
     *
     * @param map
     * @param replacements
     * @param expression
     * @return
     */
    //public List<String> processTemplate(
    //        TermMap map, List<String> replacements, String expression);
    
    /**
     *
     * @param expression
     * @param template
     * @param termType
     * @param referenceFormulation
     * @param replacement
     * @return
     */
    //public String processTemplate(String expression, String template, 
    //        String termType, QLTerm referenceFormulation, String replacement);
    
    /**
     * process a predicate object map
     * @param dataset
     * @param subject the subject created by the subject map
     * @param pom the predicate object map
     * @param node 
     */
    public void processPredicateObjectMap(
            RMLDataset dataset, Resource subject, PredicateObjectMap pom, 
            Object node, TriplesMap map, String[] exeTriplesMap);
    
    /**
     *
     * @param predicateMap
     * @param node
     * @return
     */
    //public List<URI> processPredicateMap(PredicateMap predicateMap, Object node);
    
    /**
     *
     * @param dataset
     * @param subject
     * @param predicate
     * @param pom
     * @param node
     */
    //public void processPredicateObjectMap_ObjMap(
    //        RMLDataset dataset, Resource subject, URI predicate,
    //        PredicateObjectMap pom, Object node);
    
    /**
     *
     * @param value
     * @param valueList
     * @param termMap
     * @return
     */
    //public List<Value> applyTermType(
    //        String value, List<Value> valueList, TermMap termMap);
    
    /**
     *
     * @param value
     * @return
     */
    //public String cleansing(String value);

}
