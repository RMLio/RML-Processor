/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.condition;

import static be.ugent.mmlab.rml.extractor.condition.ConditionExtractor.extractCondition;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.condition.EqualCondition;
import be.ugent.mmlab.rml.model.std.StdEqualCondition;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */
public class EqualConditionExtractor extends ConditionExtractor{
    
    //Log
    private static final Logger log = LogManager.getLogger(EqualConditionExtractor.class);
    
    public static Set<EqualCondition> extractEqualCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object, TriplesMap triplesMap){
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract equal conditions..");
        Set<EqualCondition> result = new HashSet<EqualCondition>();
        List<String> conditions = new ArrayList<String>(), 
                values = new ArrayList<String>();
        
        // Extract equal condition
        URI p = rmlMappingGraph.URIref(
                RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.EQUAL_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);

        try {
            for (Statement statement : statements) {
                conditions = extractCondition(rmlMappingGraph, object, statement);
                values = extractValue(rmlMappingGraph, object, statement);

                for (String condition : conditions) {
                    for (String value : values) {
                        if (value == null || condition == null) {
                            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                    + object.stringValue()
                                    + " must have exactly two properties condition and value. ");
                        }
                        try {
                            result.add(new StdEqualCondition(condition, value));
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(
                                    EqualConditionExtractor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        } 
        log.debug("Extract equal condition done.");
        return result;
    }
    
}
