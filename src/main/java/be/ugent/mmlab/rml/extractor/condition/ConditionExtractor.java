/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.condition;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.EqualCondition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import be.ugent.mmlab.rml.model.std.StdSplitCondition;
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
public class ConditionExtractor {

    //Log
    private static final Logger log = LogManager.getLogger(ConditionExtractor.class);

    public static Set<Condition> extractNestedCondition(
            RMLSesameDataSet rmlMappingGraph, Resource parentCondition) {
        return null;
    }

    public static List<String> extractCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object, Statement statement) {
        
        List<String> listConditions = new ArrayList<String>();

        try {
            //assigns current equal condtion
            Resource splitCond = (Resource) statement.getObject();

            //retrieves condition
            URI p = rmlMappingGraph.URIref(
                    RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.CONDITION);

            List<Statement> conditionStatements = rmlMappingGraph.tuplePattern(splitCond, p, null);
            if (conditionStatements.size() > 0) {
                for (Statement conditionStatement : conditionStatements) {
                    String condition = conditionStatement.getObject().stringValue();
                    listConditions.add(condition);
                }
            }

        } catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        } finally {
            return listConditions;
        }
    }
    
    public static List<String> extractValue(
            RMLSesameDataSet rmlMappingGraph, Resource object, Statement statement) {
        List<String> listValues = new ArrayList<String>();
        
        try {
            //assigns current equal condtion
            Resource values = (Resource) statement.getObject();
            
            //retrieves value
            URI p = rmlMappingGraph.URIref(
                    RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.VALUE);
            
            List<Statement> statements = rmlMappingGraph.tuplePattern(values, p, null);
            if (statements.size() > 0) {
                for (Statement valueStatement : statements) {
                    String condition = valueStatement.getObject().stringValue();
                    listValues.add(condition);
                }
            }
 
        }catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A valid value was expected "
                    + object.stringValue());
        } finally {
            return listValues;
        }
    }
}
