package be.ugent.mmlab.rml.vocabulary;

import org.apache.log4j.LogManager;

/**
 *
 * @author andimou
 */
public class CRMLVocabulary {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(CRMLVocabulary.class);

    // In this document, examples assume the following namespace 
    // prefix bindings unless otherwise stated:
    public static String CRML_NAMESPACE = "http://semweb.mmlab.be/ns/rml/condition#";
    
    public enum cRMLTerm implements Term{

        // RML CLASSES
        BIND_CONDITION_CLASS("BindCondition"),
        EQUAL_CONDITION_CLASS("EqualCondition"),
        PROCESS_CONDITION_CLASS("ProcessCondition"),
        SPLIT_CONDITION_CLASS("SplitCondition"),
        // RPROPERTIES
        BIND_CONDITION("bindCondition"),
        EQUAL_CONDITION("equalCondition"),
        PROCESS_CONDITION("processCondition"),
        SPLIT_CONDITION("splitCondition"),
        CONDITION("condition"),
        VALUE("value"),
        REPLACE("replace");
        
        private String displayName;

        private cRMLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
        
    }
}
