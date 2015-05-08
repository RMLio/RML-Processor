package be.ugent.mmlab.rml.extractor.condition;

import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.ProcessCondition;
import be.ugent.mmlab.rml.model.std.StdProcessCondition;
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
    
    public static Set<ProcessCondition> extractProcessCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object ) {
        
        Set<ProcessCondition> result = new HashSet<ProcessCondition>();
        List<String> conditions = new ArrayList<String>(),
                values = new ArrayList<String>();
        
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract process conditions..");

        // Extract equal condition
        URI p = rmlMappingGraph.URIref(
                RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.PROCESS_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);

        try {
            for (Statement statement : statements) {
                conditions = extractCondition(rmlMappingGraph, object, statement);
                values = extractValue(rmlMappingGraph, object, statement);

                for (String condition : conditions) {
                    Set<Condition> nestedConditions = 
                    extractNestedConditions(rmlMappingGraph, (Resource) statement.getObject());
                    
                    for (String value : values) {
                        if (value == null || condition == null) {
                            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                    + object.stringValue()
                                    + " must have exactly two properties condition and value. ");
                        }

                        try {
                            result.add(new StdProcessCondition(condition, value,nestedConditions));
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

    public static Set<Condition> extractNestedConditions(
            RMLSesameDataSet rmlMappingGraph, Resource parentCondition) {
        Set<Condition> conditions = new HashSet<Condition>();
        try{
            //retrieves nested equal conditions
            URI p = rmlMappingGraph.URIref(
                    RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.EQUAL_CONDITION);
            List<Statement> statements = rmlMappingGraph.tuplePattern(parentCondition, p, null);
            
            for(Statement statement : statements){
                conditions.addAll(EqualConditionExtractor.extractEqualCondition(
                        rmlMappingGraph, (Resource) statement.getObject()));
            }    
            
            //retrieve nested process conditions
            p = rmlMappingGraph.URIref(
                    RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.PROCESS_CONDITION);
            statements = rmlMappingGraph.tuplePattern(parentCondition, p, null);
            
            for(Statement statement : statements){
                conditions.addAll(ProcessConditionExtractor.extractProcessCondition(
                        rmlMappingGraph, (Resource) statement.getSubject()));
            }    
            
            //retrieve nested process conditions
            p = rmlMappingGraph.URIref(
                    RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.SPLIT_CONDITION);
            statements = rmlMappingGraph.tuplePattern(parentCondition, p, null);
            
            for(Statement statement : statements){
                conditions.addAll(SplitConditionExtractor.extractSplitCondition(
                        rmlMappingGraph, (Resource) statement.getSubject()));
            }  
            
        } catch(Exception ex){
            log.error(ex);
        }
        return conditions;
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
