package be.ugent.mmlab.rml.model;

import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;

/**
 *  Concrete implementation of a Logical Source
 * 
 * @author mielvandersande, andimou
 */
public class StdLogicalSource implements LogicalSource {

    private String reference;
    private QLTerm queryLanguage = QLTerm.SQL_CLASS;
    private String identifier;

    public StdLogicalSource(String reference, String identifier) {
        this.reference = reference;
        this.identifier = identifier;
    }

    public StdLogicalSource(String reference) {
        this.reference = reference;
    }

    public StdLogicalSource(String reference, String identifier, QLTerm queryLanguage) {
        this.reference = reference;
        this.identifier = identifier;
        this.queryLanguage = queryLanguage;
    }
    
    public String getReference() {
        return reference;
    }

    public QLTerm getQueryLanguage() {
        return queryLanguage;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "[StdLogicalSource : reference = " + reference
                + "; identifier" + identifier + "; queryLanguage = " + queryLanguage + "]";
    }
}
