package be.ugent.mmlab.rml.model;

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

    public StdLogicalSource(String identifier, QLTerm referenceFormulation) {
        this.referenceFormulation = referenceFormulation;
        this.identifier = identifier;
    }

    public StdLogicalSource(String reference) {
        this.iterator = reference;
    }

    public StdLogicalSource(String iterator, String identifier, QLTerm referenceFormulation) {
        this.iterator = iterator;
        this.identifier = identifier;
        this.referenceFormulation = referenceFormulation;
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
                + "; identifier" + identifier + "; referenceFormulation = " + referenceFormulation + "]";
    }
}
