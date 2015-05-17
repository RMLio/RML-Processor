package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.EqualCondition;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdEqualCondition extends StdCondition implements EqualCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdEqualCondition.class);
    
    public StdEqualCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
    }
    
    public StdEqualCondition(String condition, String value, Set<Condition> nestedConditions) 
            throws Exception {
        setCondition(condition);
        setValue(value);
        setNestedConditions(nestedConditions);
    }
    
    private void setValue(String value) throws Exception {
        if (value == null) {
            throw new Exception(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "An equal condition must "
                    + "have a value.");
        }
        this.value = value;
    }
    
}
