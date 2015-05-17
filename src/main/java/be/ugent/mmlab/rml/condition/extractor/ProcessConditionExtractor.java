package be.ugent.mmlab.rml.condition.extractor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.ProcessCondition;
import be.ugent.mmlab.rml.model.std.StdProcessCondition;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.CRMLVocabulary;
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
public class ProcessConditionExtractor extends ConditionExtractor {
    
    //Log
    private static final Logger log = LogManager.getLogger(ProcessConditionExtractor.class);
    
    public static Set<ProcessCondition> extractProcessCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object ) {
        
        Set<ProcessCondition> result = new HashSet<ProcessCondition>();
        List<String> conditions = new ArrayList<String>(),
                values = new ArrayList<String>();
        
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract process conditions..");

        // Extract process condition
        URI p = rmlMappingGraph.URIref(
                CRMLVocabulary.CRML_NAMESPACE + CRMLVocabulary.cRMLTerm.PROCESS_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);

        try {
            for (Statement statement : statements) {
                conditions = extractCondition(rmlMappingGraph, object, statement);
                values = extractValue(rmlMappingGraph, object, statement);
                
                Set<Condition> nestedConditions = 
                    extractNestedConditions(rmlMappingGraph, (Resource) statement.getObject());

                for (String condition : conditions) {
                    for (String value : values) {
                        if (value == null || condition == null) {
                            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                    + object.stringValue()
                                    + " must have exactly two properties condition and value. ");
                        }

                        try {
                            result.add(new StdProcessCondition(condition, value, nestedConditions));
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(
                                    ProcessConditionExtractor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        }
        log.debug("Extracting process condition done.");
        return result;
    }
    
}
