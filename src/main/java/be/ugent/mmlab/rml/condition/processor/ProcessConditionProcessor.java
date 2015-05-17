package be.ugent.mmlab.rml.condition.processor;

import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.ProcessCondition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public static List<String> processConditions(TermMap map, String replacement) {
        HashSet<ProcessCondition> processConditions = map.getProcessConditions();
        List<String> stringList = new  ArrayList<String>(), newStringList = new  ArrayList<String>();
        
        for (ProcessCondition processCondition : processConditions) {
            replacement = processCondition(processCondition,replacement);
            stringList = new  ArrayList<String>();
            stringList.add(replacement);
            newStringList = processNestedConditions(processCondition, stringList);
        }
        return newStringList;
    }
    
    public static List<String> processConditions(LogicalSource source, String replacement) {
        Set<ProcessCondition> processConditions = source.getProcessConditions();
        List<String> stringList = new  ArrayList<String>(), newStringList = new  ArrayList<String>();
        
        for (Condition processCondition : processConditions) {
            replacement = processCondition(processCondition,replacement);
            stringList = new  ArrayList<String>();
            stringList.add(replacement);
            newStringList = processNestedConditions(processCondition, stringList);
        }
        return newStringList;
    }
       
    public static String processCondition(Condition processCondition, String replacement) {

        String condition = processCondition.getCondition();
        String value = processCondition.getValue();

        Pattern pattern = Pattern.compile(condition);
        Matcher matcher = pattern.matcher(replacement);

        if (matcher.find()) { //&& matcher.matches()) {
            try {
                if (value.contains("\\L$")) {
                    replacement = replacement.toLowerCase();
                } else {
                    replacement = matcher.replaceAll(value);
                }
            } catch (Exception ex) {
                log.debug(ex);
                return replacement;
            }
        }
        return replacement;
    }
}
