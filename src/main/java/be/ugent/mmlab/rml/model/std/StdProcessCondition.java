package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.ProcessCondition;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdProcessCondition extends StdCondition implements ProcessCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdProcessCondition.class);
    
    public StdProcessCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
    }
    
    public StdProcessCondition(String condition, String value, Set<Condition> nestedConditions) 
            throws Exception {
        setCondition(condition);
        setValue(value);
        setNestedConditions(nestedConditions);
    }
    
    private void setValue(String value) throws Exception {
        if (value == null) {
            throw new Exception(
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "A process condition must "
                    + "have a value.");
        }
        this.value = value;
    }
    
}
