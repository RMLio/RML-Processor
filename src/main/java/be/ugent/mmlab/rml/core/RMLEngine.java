package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLEngine;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Engine that will perform the mapping starting from the TermMaps
 * 
 * @author mielvandersande
 */
public class RMLEngine {

    // Log
    private static Log log = LogFactory.getLog(R2RMLEngine.class);
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    private String baseIRI;
    
    //Hashmap containing the identifiers for files
    //There are probably better ways to do this than a static variable
    public static HashMap<String,String> fileMap = new HashMap<String, String>();

    /**
     * Generate RDF based on a RML mapping
     * 
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @return dataset containing the triples
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException 
     */
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping,
            String baseIRI) throws SQLException,
            R2RMLDataError, UnsupportedEncodingException {
        return runRMLMapping(rmlMapping, baseIRI, null);
    }

    /**
     * 
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @param pathToNativeStore path if triples have to be stored in sesame triple store instead of memory
     * @return
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException 
     */
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore) throws SQLException,
            R2RMLDataError, UnsupportedEncodingException {
        log.debug("[R2RMLEngine:runR2RMLMapping] Run R2RML mapping... ");
        if (rmlMapping == null) {
            throw new IllegalArgumentException(
                    "[R2RMLEngine:runR2RMLMapping] No R2RML Mapping object found.");
        }
        if (baseIRI == null) {
            throw new IllegalArgumentException(
                    "[R2RMLEngine:runR2RMLMapping] No base IRI found.");
        }

        SesameDataSet sesameDataSet = null;
        // Update baseIRI
        this.baseIRI = baseIRI;
        // Check if use of native store is required
        if (pathToNativeStore != null) {
            log.debug("[R2RMLEngine:runR2RMLMapping] Use native store "
                    + pathToNativeStore);
            sesameDataSet = new SesameDataSet(pathToNativeStore, false);
        } else {
            sesameDataSet = new SesameDataSet();
        }

        // Explore RML Mapping TriplesMap objects  
        generateRDFTriples(sesameDataSet, rmlMapping);


        log.debug("[R2RMLEngine:runR2RMLMapping] R2RML mapping done. ");
        return sesameDataSet;
    }
    /**
     * This process adds RDF triples to the output dataset. Each generated
     * triple is placed into one or more graphs of the output dataset. The
     * generated RDF triples are determined by the following algorithm.
     *
     * @param sesameDataSet
     * @param rmlMapping
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException
     */
    private void generateRDFTriples(SesameDataSet sesameDataSet,
            RMLMapping r2rmlMapping) throws SQLException, R2RMLDataError,
            UnsupportedEncodingException {
        
        log.debug("[R2RMLEngine:generateRDFTriples] Generate RDF triples... ");
        int delta = 0;
        
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        for (TriplesMap triplesMap : r2rmlMapping.getTriplesMaps()) {
            
            RMLProcessor processor = factory.create(triplesMap.getLogicalSource().getQueryLanguage());
            
            processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor));

            log.info("[R2RMLEngine:generateRDFTriples] "
                    + (sesameDataSet.getSize() - delta)
                    + " triples generated for " + triplesMap.getName());
            delta = sesameDataSet.getSize();
        }
    }
}
