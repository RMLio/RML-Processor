package be.ugent.mmlab.rml.processor.condition;

import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
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
    
    public static String[] processSplitCondition(TermMap map, String value) {
        HashSet<SplitCondition> splitConditions = map.getSplitConditions();
        String[] list = null;
        
        if (splitConditions != null) {
            for (SplitCondition splitCondition : splitConditions) {
                String condition = splitCondition.getCondition();
                list = value.split(condition);
                
                Set<Condition> nestedConditions = splitCondition.getNestedConditions();
                if(nestedConditions != null & nestedConditions.size() > 0){
                    for (int i = 0; i < list.length ; i++) {
                        for (Condition nestedCondition : nestedConditions) {
                            switch (nestedCondition.getClass().getSimpleName()) {
                                case "StdProcessCondition":
                                    list[i] = ProcessConditionProcessor.processProcessCondition(nestedCondition, list[i]);
                                    break;
                                case "StdSplitCondition":
                                    log.error("TODO: nested split condition");
                                    //list[i] = processSplitCondition(nestedCondition, list[i]);
                                    break;
                                case "StdEqualCondition":
                                    log.error("TODO: nested equal condition");
                                    break;
                                default:
                                    log.error("unknown condition");
                            }
                        }
                    }
                        
                }
                //else
                //    log.error("empty nested conditions");
            }
        }
        //else
        //    log.error("no nested conditions found");
        
        return list;
    }
    
}
