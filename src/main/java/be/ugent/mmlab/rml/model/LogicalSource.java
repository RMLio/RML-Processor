package be.ugent.mmlab.rml.model;

import be.ugent.mmlab.rml.model.condition.BindCondition;
import be.ugent.mmlab.rml.model.condition.Condition;
import be.ugent.mmlab.rml.model.condition.EqualCondition;
import be.ugent.mmlab.rml.model.condition.ProcessCondition;
import be.ugent.mmlab.rml.model.condition.SplitCondition;
import be.ugent.mmlab.rml.vocabulary.QLVocabulary;
import java.util.Set;

/**
 *
 * @author mielvandersande
 */
public interface LogicalSource {

    /**
     * Every logical source has an expression resulting in a list of iterating
     * values.
     */
    public String getReference();

    /**
     * Every logical source has an identifier, which is a schema-qualified name
     * pointing at a source.
     */
    public String getIdentifier();

    /**
     * Every logical source can indicate how its expression should be
     * interpreted
     */
    public QLVocabulary.QLTerm getReferenceFormulation();
    
    public String getSplitCondition();
    
    /**
     *
     * @return
     */
    public Set<EqualCondition> getEqualConditions();
        
    /**
     *
     * @return
     */
    public Set<ProcessCondition> getProcessConditions();
        
    /**
     *
     * @return
     */
    public Set<SplitCondition> getSplitConditions();
        
    /**
     *
     * @return
     */
    public Set<BindCondition> getBindConditions();
    
    /**
     *
     * @return
     */
    public Set<Condition> getConditions();
}
