/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLEngine;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mielvandersande
 */
public class RMLEngine {
    
    // Log
    private static Log log = LogFactory.getLog(R2RMLEngine.class);
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    private String baseIRI;
    
    public SesameDataSet runRMLMapping(R2RMLMapping r2rmlMapping,
            String baseIRI, String pathToNativeStore) throws SQLException,
            R2RMLDataError, UnsupportedEncodingException {
        log.debug("[R2RMLEngine:runR2RMLMapping] Run R2RML mapping... ");
        if (r2rmlMapping == null) {
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

        // Explore R2RML Mapping TriplesMap objects
        //generateRDFTriples(sesameDataSet, r2rmlMapping);


        log.debug("[R2RMLEngine:runR2RMLMapping] R2RML mapping done. ");
        return sesameDataSet;
    }

   
        /**
     * This process adds RDF triples to the output dataset. Each generated
     * triple is placed into one or more graphs of the output dataset. The
     * generated RDF triples are determined by the following algorithm.
     *
     * @param sesameDataSet
     * @param r2rmlMapping
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException
     */
//    private void generateRDFTriples(SesameDataSet sesameDataSet,
//            R2RMLMapping r2rmlMapping) throws SQLException, R2RMLDataError,
//            UnsupportedEncodingException {
//        
//        log.debug("[R2RMLEngine:generateRDFTriples] Generate RDF triples... ");
//        int delta = 0;
//        
//        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
//        
//        for (TriplesMap triplesMap : r2rmlMapping.getTriplesMaps()) {
//                    RMLProcessor processor = factory.create(QLTerm.valueOf(""));
//            
//            processor.execute(sesameDataSet, triplesMap);
//
//            log.info("[R2RMLEngine:generateRDFTriples] "
//                    + (sesameDataSet.getSize() - delta)
//                    + " triples generated for " + triplesMap.getName());
//            delta = sesameDataSet.getSize();
//        }
//    }
    
    
}
