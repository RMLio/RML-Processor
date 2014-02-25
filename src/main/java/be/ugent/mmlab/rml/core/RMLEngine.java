package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.dataset.FileSesameDataset;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Properties;
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
    
    //Properties containing the identifiers for files
    //There are probably better ways to do this than a static variable
    private static Properties fileMap = new Properties();

    public static Properties getFileMap(){
        return fileMap;
    }
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
        return runRMLMapping(rmlMapping, baseIRI, null, false);
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
            String baseIRI, String pathToNativeStore, boolean filebased) throws SQLException,
            R2RMLDataError, UnsupportedEncodingException {
        long startTime = System.nanoTime();
        
        log.debug("[RMLEngine:runRMLMapping] Run RML mapping... ");
        if (rmlMapping == null) {
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No RML Mapping object found.");
        }
        if (baseIRI == null) {
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No base IRI found.");
        }

        SesameDataSet sesameDataSet = null;
        // Update baseIRI
        this.baseIRI = baseIRI;
        
        //MVS: Check if output goes directly to file
        if (filebased) {
            log.debug("[RMLEngine:runRMLMapping] Use direct file "
                    + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore);
        } else if (pathToNativeStore != null) { // Check if use of native store is required
            log.debug("[RMLEngine:runRMLMapping] Use native store "
                    + pathToNativeStore);
            sesameDataSet = new SesameDataSet(pathToNativeStore, false);
        } else {
            sesameDataSet = new SesameDataSet();
        }
        
        // Explore RML Mapping TriplesMap objects  
        generateRDFTriples(sesameDataSet, rmlMapping);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        log.debug("[RMLEngine:runRMLMapping] RML mapping done! Generated " + sesameDataSet.getSize() +" in " + ((double) duration)/1000000000 +"s . ");
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
        
        log.debug("[RMLEngine:generateRDFTriples] Generate RDF triples... ");
        int delta = 0;
        
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        for (TriplesMap triplesMap : r2rmlMapping.getTriplesMaps()) {
            System.out.println("XPath Processor triplesMap ");
            RMLProcessor processor = factory.create(triplesMap.getLogicalSource().getQueryLanguage());
            System.out.println("XPath Processor filename " + (triplesMap.getLogicalSource().getIdentifier().toString()));
            //RMLEngine.getFileMap().put(triplesMap.getLogicalSource().getIdentifier(), getClass().getResource(triplesMap.getLogicalSource().getIdentifier()).getFile());
            System.out.println("[RMLEngine:FileMap] " + RMLEngine.getFileMap());
            String fileName = getClass().getResource(map.getLogicalSource().getIdentifier()).getFile();
            processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor), fileName);

            log.info("[RMLEngine:generateRDFTriples] "
                    + (sesameDataSet.getSize() - delta)
                    + " triples generated for " + triplesMap.getName());
            delta = sesameDataSet.getSize();
        }
//        try {
//            sesameDataSet.closeRepository();
//        } catch (RepositoryException ex) {
//            log.error("[RMLEngine:generateRDFTriples] Cannot close output repository", ex);
//        }
    }
}
