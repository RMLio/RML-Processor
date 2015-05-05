/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.condition.ProcessCondition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdProcessCondition extends StdCondition implements ProcessCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdProcessCondition.class);
    
    private String value;
    
    public StdProcessCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
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
    

        public String getValue() {
            return value;
    }

    
}
