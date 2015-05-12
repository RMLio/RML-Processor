
/**
 * *************************************************************************
 *
 * RML, R2RML & QL Vocabulary
 *
 * The R2RML vocabulary is the set of IRIs defined in this specification that
 * start with the rr: namespace IRI: http://www.w3.org/ns/r2rml#
 *
 ***************************************************************************
 */
package be.ugent.mmlab.rml.vocabulary;

import org.apache.log4j.LogManager;

public class RMLVocabulary {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(RMLVocabulary.class);

    // In this document, examples assume the following namespace 
    // prefix bindings unless otherwise stated:
    public static String RML_NAMESPACE = "http://semweb.mmlab.be/ns/rml#";
    public static String QL_NAMESPACE = "http://semweb.mmlab.be/ns/ql#";
    public static String CRML_NAMESPACE = "http://semweb.mmlab.be/ns/rml/condition#";
    public static String R2RML_NAMESPACE = "http://www.w3.org/ns/r2rml#";
    public static String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    public static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
    public static String EX_NAMESPACE = "http://example.com/ns#"; // By convention

    public static QLTerm getQLTerms(String stringValue) {
        for (QLTerm term : QLTerm.values()){
            if (stringValue.equals(QL_NAMESPACE + term)){
                return term;
            }
        }
        return null;
    }

    public interface Term {
    }

    public enum RMLTerm implements Term{

        // RML CLASSES
        LOGICAL_SOURCE_CLASS("LogicalSource"),
        // RPROPERTIES
        REFERENCE_FORMULATION("referenceFormulation"),
        LOGICAL_SOURCE("logicalSource"),
        REFERENCE("reference"),
        ITERATOR("iterator"),
        VERSION("version"),
        SOURCE("source"), 
        PROCESS("process"),
        REPLACE("replace"),
        SPLIT("split");
        
        private String displayName;

        private RMLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
        
    }

    public enum QLTerm {

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
        //PROCESS("process"),
        REPLACE("replace");
        //SPLIT("split");
        
        private String displayName;

        private cRMLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
        
    }

    public enum R2RMLTerm implements Term{
        // CLASSES

        GRAPH_MAP_CLASS("GraphMap"),
        JOIN_CLASS("Join"),
        LOGICAL_TABLE_CLASS("LogicalTable"),
        OBJECT_MAP_CLASS("ObjectMap"),
        PREDICATE_MAP_CLASS("PredicateMap"),
        PREDICATE_OBJECT_MAP_CLASS("PredicateObjectMap"),
        REF_OBJECT_MAP_CLASS("RefObjectMap"),
        SUBJECT_MAP_CLASS("SubjectMap"),
        TRIPLES_MAP_CLASS("TriplesMap"),
        // PROPERTIES
        CLASS("class"),
        CHILD("child"),
        COLUMN("column"),
        DATATYPE("datatype"),
        CONSTANT("constant"),
        GRAPH("graph"),
        GRAPH_MAP("graphMap"),
        GRAPH_COLUMN("graphColumn"),
        GRAPH_TEMPLATE("graphTemplate"),
        INVERSE_EXPRESSION("inverseExpression"),
        JOIN_CONDITION("joinCondition"),
        LANGUAGE("language"),
        LOGICAL_TABLE("logicalTable"),
        OBJECT("object"),
        OBJECT_MAP("objectMap"),
        PARENT("parent"),
        PARENT_TRIPLES_MAP("parentTriplesMap"),
        PREDICATE("predicate"),
        PREDICATE_MAP("predicateMap"),
        PREDICATE_OBJECT_MAP("predicateObjectMap"),
        SQL_QUERY("sqlQuery"),
        SQL_VERSION("sqlVersion"),
        SUBJECT("subject"),
        SUBJECT_MAP("subjectMap"),
        TABLE_NAME("tableName"),
        TEMPLATE("template"),
        TERM_TYPE("termType"),
        // SPECIAL
        DEFAULT_GRAPH("defaultGraph"),
        IRI("IRI"),
        BLANK_NODE("BlankNode"),
        LITERAL("Literal"),
        //FROM ANOTHER ONTOLOGY
        TYPE("type");
        private String displayName;

        private R2RMLTerm(String displayName) {
            this.displayName = displayName;
        }

        public String toString() {
            return displayName;
        }
        
    }
}
