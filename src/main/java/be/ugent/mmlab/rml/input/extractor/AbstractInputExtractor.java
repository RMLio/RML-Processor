package be.ugent.mmlab.rml.input.extractor;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.LogManager;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */
abstract public class AbstractInputExtractor implements InputExtractor {

    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(AbstractInputExtractor.class);

    public List<Statement> getInput(RMLSesameDataSet rmlMappingGraph, String reference, TriplesMap map) {
        URI o = rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLVocabulary.RMLTerm.SOURCE);
        URI p = rmlMappingGraph.URIref("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        List<Statement> statements = rmlMappingGraph.tuplePattern(null, p, o);
        return statements;
    }

    /**
     *
     * @param stringTemplate
     * @return
     */
    public Set<String> extractStringTemplate(String stringTemplate) {
        Set<String> result = new HashSet<String>();
        // Curly braces that do not enclose column names MUST be
        // escaped by a backslash character (“\”).
        stringTemplate = stringTemplate.replaceAll("\\\\\\{", "");
        stringTemplate = stringTemplate.replaceAll("\\\\\\}", "");

        if (stringTemplate != null) {
            StringTokenizer st = new StringTokenizer(stringTemplate, "{}", true);
            boolean keepNext = false;
            String next = null;
            while (st.hasMoreElements()) {
                String element = st.nextElement().toString();
                if (keepNext) {
                    next = element;
                }
                keepNext = element.equals("{");
                if (element.equals("}") && element != null) {
                    log.debug("Extracted variable name "
                            + next + " from string template " + stringTemplate);
                    result.add(next);
                    next = null;
                }
            }
        }
        return result;
    }

}
