package be.ugent.mmlab.rml.processor.condition;

import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.ProcessCondition;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author andimou
 */
public class ProcessConditionProcessor extends ConditionProcessor{
    
    // Log
    private static Log log = LogFactory.getLog(ProcessConditionProcessor.class);
    
    /**
     *
     * @param map
     * @param replacement
     * @return
     */
    public static String processConditions(TermMap map, String replacement) {
        HashSet<ProcessCondition> processConditions = map.getProcessConditions();
        for (ProcessCondition processCondition : processConditions) {
            replacement = processCondition(processCondition,replacement);
        }
        return replacement;
    }
    
    public static String processCondition(Condition processCondition, String replacement) {

            String condition = processCondition.getCondition();
            String value = processCondition.getValue();
            
            Pattern pattern = Pattern.compile(condition);
            Matcher matcher = pattern.matcher(replacement);
            
            if (matcher.find() ) { //&& matcher.matches()) {
                try {
                    if(value.contains("\\L$")){
                        replacement = replacement.toLowerCase();
                    }
                    else
                        replacement = matcher.replaceAll(value);
                } catch (Exception ex) {
                    log.debug(ex);
                    return replacement;
                }
            }
        return replacement;
    }
    
}
