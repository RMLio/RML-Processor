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

import be.ugent.mmlab.rml.extractor.RMLDocExtractor;
import be.ugent.mmlab.rml.extractor.RMLMappingExtractor;
import be.ugent.mmlab.rml.extractor.RMLUnValidatedMappingExtractor;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.R2RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.Vocab.R2RMLTerm;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class RMLMappingFactory {

    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(RMLMappingFactory.class);
    
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
     * @throws IOException
     * @throws RDFParseException
     * @throws RepositoryException
     */
    public RMLMapping extractRMLMapping(String fileToRMLFile)
            throws RepositoryException, RDFParseException, IOException, Exception {
        // Load RDF data from R2RML Mapping document
        RMLSesameDataSet rmlMappingGraph ;
        
        RMLDocExtractor mapDocExtractor = new RMLDocExtractor() ;
        rmlMappingGraph = mapDocExtractor.getMappingDoc(fileToRMLFile, RDFFormat.TURTLE);

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Number of RML triples in file "
                + fileToRMLFile + " : " + rmlMappingGraph.getSize());
        // Transform RDF with replacement shortcuts
        rmlMappingGraph = extractor.replaceShortcuts(rmlMappingGraph);
        // Run few tests to help user in its RDF syntax
        launchPreChecks(rmlMappingGraph);
               
        // Construct RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = extractor.extractTriplesMapResources(rmlMappingGraph);
               
        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Number of RML triples with "
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

    private static void launchPreChecks(SesameDataSet rmlMappingGraph) throws Exception {
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                    + R2RMLTerm.SUBJECT_MAP);
            List<Statement> otherStatements = rmlMappingGraph.tuplePattern(
                    s.getSubject(), p, null);
            if (otherStatements.isEmpty()) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "You have a triples map without subject map : "
                        + s.getSubject().stringValue() + ".");
            }
        }
    }


    /*private static ReferenceIdentifier extractReferenceIdentifier(SesameDataSet rmlMappingGraph, Resource resource) throws InvalidR2RMLStructureException {
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
    }*/
    

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
            SesameDataSet rmlMappingGraph, Resource termType, Enum term) {

        URI p = getTermURI(rmlMappingGraph, term);

        List<Statement> statements = rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + 
                    "[RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        String result = statements.get(0).getObject().stringValue();
        if (log.isDebugEnabled()) {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "[RMLMappingFactory:extractLiteralFromTermMap] Extracted "
                    + term + " : " + result);
        }
        return result;
    }

    
    private static URI getTermURI(SesameDataSet rmlMappingGraph, Enum term) {
        String namespace = R2RMLVocabulary.R2RML_NAMESPACE;

        if (term instanceof RMLVocabulary.RMLTerm) {
            namespace = RMLVocabulary.RML_NAMESPACE;
        } else if (!(term instanceof R2RMLTerm)) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " +
                    term + " is not valid.");
        }

        return rmlMappingGraph
                .URIref(namespace + term);
    }
}
