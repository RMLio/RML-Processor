package be.ugent.mmlab.rml.model.condition;

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
