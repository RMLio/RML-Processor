/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.condition.EqualCondition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdEqualCondition implements EqualCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdEqualCondition.class);
    
    private String condition;
    private String value;
    
    public StdEqualCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
    }
    
    private void setValue(String value) throws Exception {
        if (value == null) {
            throw new Exception(
                    "[StdEqualCondition:setParent] An equal condition must "
                    + "have a value.");
        }
        this.value = value;
    }
    
    private void setCondition(String condition) throws Exception {
        if (condition == null) {
            throw new Exception(
                    "[StdEqualCondition:construct] A equal condition must "
                    + "have a condition value.");
        }
        this.condition = condition;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public String getValue() {
        return value;
    }

    
}
