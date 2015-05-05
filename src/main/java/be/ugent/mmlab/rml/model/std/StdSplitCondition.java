/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.condition.SplitCondition;
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

    
}
