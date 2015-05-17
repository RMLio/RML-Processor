package be.ugent.mmlab.rml.extractor.condition;

import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import be.ugent.mmlab.rml.model.std.StdSplitCondition;
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
public class SplitConditionExtractor extends ConditionExtractor {
    
    //Log
    private static final Logger log = LogManager.getLogger(SplitConditionExtractor.class);
    
    public static Set<SplitCondition> extractSplitCondition(
            RMLSesameDataSet rmlMappingGraph, Resource object){
        log.debug(
                Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Extract split conditions..");
        List<String> listCondition = new ArrayList<String>();
        Set<SplitCondition> result = new HashSet<SplitCondition>();
        
        // Extract split condition
        URI p = rmlMappingGraph.URIref(
                CRMLVocabulary.CRML_NAMESPACE + CRMLVocabulary.cRMLTerm.SPLIT_CONDITION);
        List<Statement> statements = rmlMappingGraph.tuplePattern(object, p, null);
        
        for (Statement statement : statements) {
            listCondition = extractCondition(rmlMappingGraph, object, statement);
            Set<Condition> nestedConditions = 
                    extractNestedConditions(rmlMappingGraph, (Resource) statement.getObject());
            
            for (String condition : listCondition) {
                try {
                    result.add(new StdSplitCondition(condition, nestedConditions));
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(
                            ProcessConditionExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        log.debug("Extracting split condition done.");
        return result;
    }
    
}
