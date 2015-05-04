package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.QLTerm;

/**
 *  Concrete implementation of a Logical Source
 * 
 * @author mielvandersande, andimou
 */
public class StdLogicalSource implements LogicalSource {

    private String iterator;
    private QLTerm referenceFormulation = QLTerm.SQL_CLASS;
    private String identifier;
    private String splitCondition;

    public StdLogicalSource(String identifier, QLTerm referenceFormulation) {
        this.referenceFormulation = referenceFormulation;
        this.identifier = identifier;
    }
    
    public StdLogicalSource(String identifier, QLTerm referenceFormulation, String splitCondition) {
        this.referenceFormulation = referenceFormulation;
        this.identifier = identifier;
        this.splitCondition = splitCondition;
    }

    public StdLogicalSource(String reference) {
        this.iterator = reference;
    }

    public StdLogicalSource(String iterator, String identifier, QLTerm referenceFormulation) {
        this.iterator = iterator;
        this.identifier = identifier;
        this.referenceFormulation = referenceFormulation;
    }
    
    public StdLogicalSource(String iterator, String identifier, 
            QLTerm referenceFormulation, String splitCondition) {
        this.iterator = iterator;
        this.identifier = identifier;
        this.referenceFormulation = referenceFormulation;
        this.splitCondition = splitCondition;
    }
    
    @Override
    public String getReference() {
        return iterator;
    }

    @Override
    public QLTerm getReferenceFormulation() {
        return referenceFormulation;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "[StdLogicalSource : iterator = " + iterator
                + "; identifier" + identifier + "; referenceFormulation = " + referenceFormulation 
                + "; splitCondition = " + splitCondition + "]";
    }
    
    @Override
    public String getSplitCondition() {
        return splitCondition;
    }
}
