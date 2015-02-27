/**
 * *************************************************************************
 *
 * RML : RML Mapping Factory abstract class
 *
 * Factory responsible of RML Mapping generation.
 *
 * based on R2RMLMappingFactory in db2triples
 * 
 * modified by andimou
 *
 ***************************************************************************
 */
package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.RMLextractor.RMLDocExtractor;
import be.ugent.mmlab.rml.RMLextractor.RMLMappingExtractor;
import be.ugent.mmlab.rml.RMLextractor.RMLUnValidatedMappingExtractor;
import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.StdGraphMap;
import be.ugent.mmlab.rml.model.StdJoinCondition;
import be.ugent.mmlab.rml.model.StdLogicalSource;
import be.ugent.mmlab.rml.model.StdObjectMap;
import be.ugent.mmlab.rml.model.StdPredicateMap;
import be.ugent.mmlab.rml.model.StdPredicateObjectMap;
import be.ugent.mmlab.rml.model.StdReferencingObjectMap;
import be.ugent.mmlab.rml.model.StdSubjectMap;
import be.ugent.mmlab.rml.model.StdTriplesMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifierImpl;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.RMLTerm;
import be.ugent.mmlab.rml.vocabulary.Vocab.R2RMLTerm;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class RMLMappingFactory {

    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(RMLMappingFactory.class);
    // Value factory
    private static ValueFactory vf = new ValueFactoryImpl();
    
    private RMLMappingExtractor extractor;
    
    public RMLMappingFactory(){
        this.extractor = new RMLUnValidatedMappingExtractor();
    }

    /**
     * Extract RML Mapping object from a RML file written with Turtle syntax.
     *
     * Important : The R2RML vocabulary also includes the following R2RML
     * classes, which represent various R2RML mapping constructs. Using these
     * classes is optional in a mapping graph. The applicable class of a
     * resource can always be inferred from its properties. Consequently, in
     * order to identify each triple type, a rule will be used to extract the
     * applicable class of a resource.
     *
     * @param fileToRMLFile
     * @return
     * @throws InvalidR2RMLSyntaxException
     * @throws InvalidR2RMLStructureException
     * @throws R2RMLDataError
     * @throws IOException
     * @throws RDFParseException
     * @throws RepositoryException
     */
    public RMLMapping extractRMLMapping(String fileToRMLFile)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError, RepositoryException, RDFParseException, IOException {
        // Load RDF data from R2RML Mapping document
        RMLSesameDataSet rmlMappingGraph ;
        RMLDocExtractor inputExtractor = new RMLDocExtractor() ;
        rmlMappingGraph = inputExtractor.getMappingDoc(fileToRMLFile, RDFFormat.TURTLE);

        log.debug("[RMLMappingFactory:extractRMLMapping] Number of R2RML triples in file "
                + fileToRMLFile + " : " + rmlMappingGraph.getSize());
        // Transform RDF with replacement shortcuts
        rmlMappingGraph = extractor.replaceShortcuts(rmlMappingGraph);
        // Run few tests to help user in its RDF syntax
        launchPreChecks(rmlMappingGraph);
               
        // Construct RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = extractor.extractTriplesMapResources(rmlMappingGraph);

        log.debug("[RMLMappingFactory:extractRMLMapping] Number of RML triples with "
                + " type "
                + R2RMLTerm.TRIPLES_MAP_CLASS
                + " in file "
                + fileToRMLFile + " : " + triplesMapResources.size());
        // Fill each triplesMap object
        for (Resource triplesMapResource : triplesMapResources.keySet()) // Extract each triplesMap
        {
            extractor.extractTriplesMap(rmlMappingGraph, triplesMapResource,
                    triplesMapResources);
        }
        // Generate RMLMapping object
        RMLMapping result = new RMLMapping(triplesMapResources.values());
        return result;
    }

    private static void launchPreChecks(SesameDataSet rmlMappingGraph)
            throws InvalidR2RMLStructureException {
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = rmlMappingGraph.URIref(RMLVocabulary.R2RML_NAMESPACE
                    + R2RMLTerm.SUBJECT_MAP);
            List<Statement> otherStatements = rmlMappingGraph.tuplePattern(
                    s.getSubject(), p, null);
            if (otherStatements.isEmpty()) {
                throw new InvalidR2RMLStructureException(
                        "[RMLMappingFactory:launchPreChecks] You have a triples map without subject map : "
                        + s.getSubject().stringValue() + ".");
            }
        }
    }


    private static ReferenceIdentifier extractReferenceIdentifier(SesameDataSet rmlMappingGraph, Resource resource) throws InvalidR2RMLStructureException {
        //MVS: look for a reference or column, prefer rr:column
        String columnValueStr = extractLiteralFromTermMap(rmlMappingGraph, resource, R2RMLTerm.COLUMN);
        String referenceValueStr = extractLiteralFromTermMap(rmlMappingGraph, resource, RMLTerm.REFERENCE);

        if (columnValueStr != null && referenceValueStr != null) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractReferenceIdentifier] "
                    + resource
                    + " has a reference and column defined.");
        }

        //MVS: use the generic ReferenceIdentifier to represent rr:column or rml:reference
        if (columnValueStr != null) {
            return ReferenceIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        }

        return ReferenceIdentifierImpl.buildFromR2RMLConfigFile(referenceValueStr);
    }
    

    /**
     * Extract content literal from a term type resource.
     *
     * @param rmlMappingGraph
     * @param termType
     * @param term
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static String extractLiteralFromTermMap(
            SesameDataSet rmlMappingGraph, Resource termType, Enum term)
            throws InvalidR2RMLStructureException {

        URI p = getTermURI(rmlMappingGraph, term);

        List<Statement> statements = rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        String result = statements.get(0).getObject().stringValue();
        if (log.isDebugEnabled()) {
            log.debug("[RMLMappingFactory:extractLiteralFromTermMap] Extracted "
                    + term + " : " + result);
        }
        return result;
    }

    
    private static URI getTermURI(SesameDataSet rmlMappingGraph, Enum term) throws InvalidR2RMLStructureException {
        String namespace = RMLVocabulary.R2RML_NAMESPACE;

        if (term instanceof RMLVocabulary.RMLTerm) {
            namespace = RMLVocabulary.RML_NAMESPACE;
        } else if (!(term instanceof R2RMLTerm)) {
            throw new InvalidR2RMLStructureException(
                    "[RMLMappingFactory:extractValueFromTermMap] " + term + " is not valid.");
        }

        return rmlMappingGraph
                .URIref(namespace + term);
    }
}
