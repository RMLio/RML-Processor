package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.condition.model.Condition;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdCondition.class);
    
    protected String condition;
    protected String value;
    protected Set<Condition> nestedConditions ;
    
    protected void setCondition(String condition) throws Exception {
        if (condition == null) {
            throw new Exception(
                    "A condition must "
                    + "have a condition value.");
        }
        this.condition = condition;
    }
    
    protected void setNestedConditions(Set<Condition> nestedConditions) throws Exception {
        this.nestedConditions = nestedConditions;
    }
    
    public Set<Condition> getNestedConditions() {
        return nestedConditions;
    }
    
    public String getCondition() {
        return condition;
    }
    
    public String getValue() {
        return value;
    }    
}
