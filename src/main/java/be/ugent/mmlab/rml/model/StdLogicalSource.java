package be.ugent.mmlab.rml.model;

import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;

/**
 *  Concrete implementation of a Logical Source
 * 
 * @author mielvandersande
 */
public class StdLogicalSource implements LogicalSource {

    private String selector;
    private QLTerm queryLanguage = QLTerm.SQL_CLASS;
    private String identifier;

    public StdLogicalSource(String selector, String identifier) {
        this.selector = selector;
        this.identifier = identifier;
    }

    public StdLogicalSource(String selector) {
        this.selector = selector;
    }

    public StdLogicalSource(String selector, String identifier, QLTerm queryLanguage) {
        this.selector = selector;
        this.identifier = identifier;
        this.queryLanguage = queryLanguage;
    }
    
    

    public String getSelector() {
        return selector;
    }

    public QLTerm getQueryLanguage() {
        return queryLanguage;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "[StdLogicalSource : selector = " + selector
                + "; identifier" + identifier + "; queryLanguage = " + queryLanguage + "]";
    }
}
