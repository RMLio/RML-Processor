package be.ugent.mmlab.rml.input.extractor.concrete;

import be.ugent.mmlab.rml.input.model.InputSource;
import be.ugent.mmlab.rml.input.extractor.AbstractInputExtractor;
import be.ugent.mmlab.rml.input.model.std.SparqlSdInputSource;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.SPARQLSDVocabulary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */
public class SparqlExtractor extends AbstractInputExtractor {

    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(SparqlExtractor.class);

    @Override
    public Set<InputSource> extractInput(RMLSesameDataSet rmlMappingGraph, Resource resource) {
        Set<InputSource> inputSources = new HashSet<InputSource>();

        URI predicate = rmlMappingGraph.URIref(
                SPARQLSDVocabulary.SPARQLSD_NAMESPACE
                + SPARQLSDVocabulary.SparqlSdTerm.SPARQL_QUERY_TEMPLATE);

        List<Statement> statements = rmlMappingGraph.tuplePattern(
                (Resource) resource, predicate, null);

        for (Statement statement : statements) {
            inputSources.add(
                    new SparqlSdInputSource(resource.stringValue(), statement.getObject().stringValue()));
        }

        return inputSources;

    }
}
