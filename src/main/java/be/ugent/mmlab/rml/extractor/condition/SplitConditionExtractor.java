/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.condition;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import be.ugent.mmlab.rml.model.std.StdSplitCondition;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
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
public class SplitConditionExtractor {
    
    //Log
    private static final Logger log = LogManager.getLogger(ProcessConditionExtractor.class);
    
    public static Set<SplitCondition> extractCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object, TriplesMap triplesMap){
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract split conditions..");
        Set<SplitCondition> result = new HashSet<SplitCondition>();
        String condition = null;
        
        // Extract equal condition
        URI p = rmlMappingGraph.URIref(
                RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.SPLIT_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);

        try {
            for (Statement statement : statements) {
                //assigns current equal condtion
                Resource ec = (Resource) statement.getObject();

                //retrieves condition
                p = rmlMappingGraph.URIref(
                        RMLVocabulary.CRML_NAMESPACE + RMLVocabulary.cRMLTerm.CONDITION);
                statements = rmlMappingGraph.tuplePattern(ec, p, null);
                if (statements.size() > 0) {
                    condition = statements.get(0).getObject().stringValue();

                    try {
                        result.add(new StdSplitCondition(condition));

                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(
                                ProcessConditionExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (ClassCastException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        } 
        
        log.debug("Extracting split condition done.");
        return result;
    }
    
}
