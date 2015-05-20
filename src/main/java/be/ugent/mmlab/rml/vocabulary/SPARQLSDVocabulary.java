package be.ugent.mmlab.rml.vocabulary;

import org.apache.log4j.LogManager;

/**
 *
 * @author andimou
 */
public class SPARQLSDVocabulary {
    // Log

    private static final org.apache.log4j.Logger log = LogManager.getLogger(SPARQLSDVocabulary.class);
    public static String SPARQLSD_NAMESPACE = "http://www.w3.org/ns/sparql-service-description#";

    public enum SparqlSdTerm implements Term {

        // RML CLASSES
        SERVICE_CLASS("Service"),
        // RPROPERTIES
        SPARQL_QUERY_TEMPLATE("sparqlQueryTemplate");
        private String displayName;

        private SparqlSdTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
