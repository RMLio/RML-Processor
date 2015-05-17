package be.ugent.mmlab.rml.condition.processor;

import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.EqualCondition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author andimou
 */
public class EqualConditionProcessor extends ConditionProcessor{
    
    // Log
    private static Log log = LogFactory.getLog(EqualConditionProcessor.class);
    
    public static List<String> processConditions(TermMap map, String replacement) {
        HashSet<EqualCondition> equalConditions = map.getEqualConditions();
        List<String> stringList, newStringList = null;
        
        if (equalConditions != null) {
            for (EqualCondition equalCondition : equalConditions) {
                replacement = processCondition(equalCondition, replacement);
                newStringList = new  ArrayList<String>();
                stringList = new  ArrayList<String>();
                stringList.add(replacement);
                newStringList.addAll(processNestedConditions(equalCondition, stringList));

            }
        }
        return newStringList;
    }
    
    public static List<String> processConditions(LogicalSource source, String replacement) {
        Set<EqualCondition> equalConditions = source.getEqualConditions();
        List<String> stringList, newStringList = null;
        
        if (equalConditions != null) {
            for (Condition equalCondition : equalConditions) {
                replacement = processCondition(equalCondition, replacement);
                newStringList = new  ArrayList<String>();
                stringList = new  ArrayList<String>();
                stringList.add(replacement);
                newStringList.addAll(processNestedConditions(equalCondition, stringList));

            }
        }
        return newStringList;
    }
    
    public static String processCondition(Condition equalCondition, String replacement) {

        String condition = equalCondition.getCondition();
        String value = equalCondition.getValue();
        if (replacement != null && replacement.equals(condition)) {
            replacement = value;
        }
        
        return replacement;
    }
    
}
