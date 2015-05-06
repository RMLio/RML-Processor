package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdSplitCondition extends StdCondition implements SplitCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdSplitCondition.class);
    
    private String value;
    
    public StdSplitCondition(String condition) throws Exception {
        setCondition(condition);
    }
    
    public StdSplitCondition(String condition, Set<Condition> nestedConditions) throws Exception {
        setCondition(condition);
        setNestedConditions(nestedConditions);
    }

    @Override
    public String getValue() {
        return value;
    }

    
}
