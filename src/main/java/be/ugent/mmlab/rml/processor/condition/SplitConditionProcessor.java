package be.ugent.mmlab.rml.processor.condition;

import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import static be.ugent.mmlab.rml.processor.condition.ConditionProcessor.processNestedConditions;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author andimou
 */
public class SplitConditionProcessor extends ConditionProcessor{
    
    // Log
    private static Log log = LogFactory.getLog(SplitConditionProcessor.class);
    
    public static String[] processConditions(TermMap map, String value) {
        HashSet<SplitCondition> splitConditions = map.getSplitConditions();
        String[] list = null;
        
        if (splitConditions != null) {
            for (SplitCondition splitCondition : splitConditions) {
                String condition = splitCondition.getCondition();
                list = value.split(condition);
                Set<Condition> nestedConditions = splitCondition.getNestedConditions();
                if(nestedConditions != null & nestedConditions.size() > 0){
                    list = processNestedConditions(nestedConditions, list);
                        
                }
                //else
                //    log.error("empty nested conditions");
            }
        }
        //else
        //    log.error("no nested conditions found");
        
        return list;
    }
    
    public static String[] processCondition(Condition nestedCondition, String value) {
        String[] list = null;

        String condition = nestedCondition.getCondition();
        list = value.split(condition);
        Set<Condition> nestedConditions = nestedCondition.getNestedConditions();
        if (nestedConditions != null & nestedConditions.size() > 0) {
            list = processNestedConditions(nestedConditions, list);

        }


        return list;
    }
    
}
