/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.condition.extractor;

import be.ugent.mmlab.rml.condition.model.BindCondition;
import be.ugent.mmlab.rml.model.std.StdBindCondition;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.CRMLVocabulary;
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
public class BindConditionExtractor extends ConditionExtractor {
    
    //Log
    private static final Logger log = LogManager.getLogger(BindConditionExtractor.class);
    
    public static Set<BindCondition> extractBindCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object) {

        Set<BindCondition> result = new HashSet<BindCondition>();
        List<String> values = new ArrayList<String>();

        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract bind conditions..");

        // Extract equal condition
        URI p = rmlMappingGraph.URIref(
                CRMLVocabulary.CRML_NAMESPACE + CRMLVocabulary.cRMLTerm.BIND_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);

        try {
            for (Statement statement : statements) {
                values = extractValue(rmlMappingGraph, object, statement);
                String reference = extractReference(rmlMappingGraph, object, statement);


                for (String value : values) {
                    if (value == null) {
                        log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                + object.stringValue()
                                + " must have exactly two properties condition and value. ");
                    }
                    try {
                        result.add(new StdBindCondition(null, value, reference, null));
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(
                                EqualConditionExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //}
            }
        } catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        }
        log.debug("Extract bind condition done.");
        return result;
    }
    
    public static String extractReference(
            RMLSesameDataSet rmlMappingGraph, Resource object, Statement statement) {
        String reference = null;
        
        try {
            //assigns current equal condtion
            Resource values = (Resource) statement.getObject();
            
            //retrieves value
            URI p = rmlMappingGraph.URIref(
                    RMLVocabulary.RML_NAMESPACE + RMLVocabulary.RMLTerm.REFERENCE);
            
            List<Statement> statements = rmlMappingGraph.tuplePattern(values, p, null);
            if (statements.size() > 0) {
                for (Statement valueStatement : statements) {
                    reference = valueStatement.getObject().stringValue();
                }
            }
 
        }catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A valid value was expected "
                    + object.stringValue());
        } finally {
            return reference;
        }
    }
    
}
