package be.ugent.mmlab.rml.condition.model;

import java.util.Set;

/**
 *
 * @author andimou
 */
abstract public interface Condition {
    
    /**
     *
     * @return
     */
    public String getCondition();
    
    /**
     *
     * @return
     */
    public String getValue();
    
    /**
     *
     * @return
     */
    public Set<Condition> getNestedConditions();
    
}
