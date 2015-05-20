package be.ugent.mmlab.rml.vocabulary;

import org.apache.log4j.LogManager;

/**
 *
 * @author andimou
 */
public class QLVocabulary {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(QLVocabulary.class);

    // In this document, examples assume the following namespace 
    // prefix bindings unless otherwise stated:
    public static String QL_NAMESPACE = "http://semweb.mmlab.be/ns/ql#";
    
    public enum QLTerm implements Term{

        XPATH_CLASS("XPath"),
        SQL_CLASS("SQL"),
        JSONPATH_CLASS("JSONPath"),
        CSV_CLASS("CSV"),
        CSS3_CLASS("CSS3");
        
        private String displayName;

        private QLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public static QLTerm getQLTerms(String stringValue) {
        for (QLTerm term : QLTerm.values()){
            if (stringValue.equals(QL_NAMESPACE + term)){
                return term;
            }
        }
        return null;
    }
}
