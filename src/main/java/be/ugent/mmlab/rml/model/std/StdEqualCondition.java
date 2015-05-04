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
public class StdEqualCondition extends StdCondition implements EqualCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdEqualCondition.class);
    
    private String value;
    
    public StdEqualCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
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
    

    @Override
    public String getValue() {
        return value;
    }

    
}
