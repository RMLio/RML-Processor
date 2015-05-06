package be.ugent.mmlab.rml.processor.condition;

import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.EqualCondition;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author andimou
 */
public class EqualConditionProcessor extends ConditionProcessor{
    
    // Log
    private static Log log = LogFactory.getLog(EqualConditionProcessor.class);
    
    public static String processConditions(TermMap map, String replacement) {
        HashSet<EqualCondition> equalConditions = map.getEqualConditions();
        if (equalConditions != null) {
            for (EqualCondition equalCondition : equalConditions) {
                replacement = processCondition(equalCondition, replacement);
            }
        }
        return replacement;
    }
    
    public static String processCondition(Condition equalCondition, String replacement) {

        String condition = equalCondition.getCondition();
        String value = equalCondition.getValue();
        if (replacement.equals(condition)) {
            replacement = value;
        }
        
        return replacement;
    }
    
}
