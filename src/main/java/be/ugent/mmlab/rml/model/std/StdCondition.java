/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model.std;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author andimou
 */
public class StdCondition {
    
    // Log
    private static final Logger log = LogManager.getLogger(StdEqualCondition.class);
    
    protected String condition;
    
    protected void setCondition(String condition) throws Exception {
        if (condition == null) {
            throw new Exception(
                    "A equal condition must "
                    + "have a condition value.");
        }
        this.condition = condition;
    }
    
    public String getCondition() {
        return condition;
    }
    
}
