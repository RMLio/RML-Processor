/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor.condition;

import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.EqualCondition;
import be.ugent.mmlab.rml.model.condition.ProcessCondition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Value;

/**
 *
 * @author andimou
 */
public class ConditionProcessor {
    
    // Log
    private static Log log = LogFactory.getLog(ConditionProcessor.class);
    
    public static List<String> postProcessTermMap(
            TermMap termMap, Object node, String value, List<Value> valueList) {
        String[] list ;
        String split = termMap.getSplit();
        String process = termMap.getProcess();
        String replace = termMap.getReplace();
        List<String> stringList = null;

        if (split != null) {
            list = value.split(split);
            if (replace != null && list != null) {
                Integer replaceOrder = Integer.parseInt(replace.substring(1));

                if ((replaceOrder - 1) > 0 && (replaceOrder - 1) < list.length) {
                    value = list[replaceOrder - 1];
                } else {
                    value = null;
                }
                if (stringList == null) {
                    //valueList = new ArrayList<Value>();
                    stringList = new ArrayList<String>();
                }
                if (value != null && !value.equals("")) {
                    //stringList.add(cleansing(value));
                    stringList.add(value);
                }
            }
            else {
                for (String item : list) {
                    if (stringList == null) {
                        //valueList = new ArrayList<Value>();
                        stringList = new ArrayList<String>();
                    }
                    stringList.add(item);
                }
            }
        }

        if (process != null && replace != null) {
            Pattern replacement = Pattern.compile(process);
            Matcher matcher = replacement.matcher(value);
            if (matcher.find() && matcher.matches()) {
                if (stringList == null) {
                    stringList = new ArrayList<String>();
                }
                try {
                    value = matcher.replaceAll(replace);
                } catch (Exception ex) {
                    log.debug(ex);
                    return stringList;
                }
                if (value != null && !value.equals("")) {
                    //stringList.add(cleansing(value));
                    stringList.add(value);
                } else {
                    return stringList;
                }
            }
        }
        return stringList;
    }
    
    public static List <String> processCondition(TermMap map, String value) {
        HashSet<ProcessCondition> processConditions = map.getProcessConditions();
        HashSet<SplitCondition> splitConditions = map.getSplitConditions();
        HashSet<EqualCondition> equalConditions = map.getEqualConditions();
        List <String> result = new ArrayList<>();

        if (equalConditions.size() > 0) {
            value = EqualConditionProcessor.processEqualCondition(map, value);
            result.add(value);
        }
        else if (processConditions.size() > 0){
            value = ProcessConditionProcessor.processProcessConditions(map, value);
            result.add(value);
        }
        else if (splitConditions.size() > 0) {
            String[] list = SplitConditionProcessor.processSplitCondition(map, value);
            for (String valueList : list) {
                result.add(valueList);
            }
        }

        return result;
    }
    
}
