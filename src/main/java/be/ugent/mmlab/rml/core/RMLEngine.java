package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.dataset.FileSesameDataset;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;

/**
 * Engine that will perform the mapping starting from the TermMaps
 * 
 * @author mielvandersande, andimou
 */
public class RMLEngine {

    // Log
    private static Log log = LogFactory.getLog(RMLEngine.class);
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    private String baseIRI;
    //private static boolean source_properties;
    //Properties containing the identifiers for files
    //There are probably better ways to do this than a static variable
    private static Properties fileMap = new Properties();

    public static Properties getFileMap() {
        return fileMap;
    }
    
    
    protected String getIdentifier(LogicalSource ls) {
        return RMLEngine.getFileMap().getProperty(ls.getIdentifier());
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
            R2RMLDataError, UnsupportedEncodingException, IOException {
        return runRMLMapping(rmlMapping, baseIRI, null, false);
    }

    /**
     *
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @param pathToNativeStore path if triples have to be stored in sesame
     * triple store instead of memory
     * @return
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException
     */
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore, boolean filebased) 
            throws SQLException, R2RMLDataError, UnsupportedEncodingException, IOException {
        long startTime = System.nanoTime();

        log.debug("[RMLEngine:runRMLMapping] Run RML mapping... ");
        if (rmlMapping == null) 
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No RML Mapping object found.");
        if (baseIRI == null) 
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No base IRI found.");

        SesameDataSet sesameDataSet ;
        // Update baseIRI
        this.baseIRI = baseIRI;
        log.info("RMLEngine base IRI " + baseIRI);

        if (filebased) {
            log.debug("[RMLEngine:runRMLMapping] Use direct file "
                    + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore);
        } else if (pathToNativeStore != null) { // Check if use of native store is required
            log.debug("[RMLEngine:runRMLMapping] Use native store "
                    + pathToNativeStore);
            sesameDataSet = new SesameDataSet(pathToNativeStore, false);
        } else {
            log.debug("[RMLEngine:runRMLMapping] Use default store (memory) ");
            sesameDataSet = new RMLSesameDataSet();
        }
        // Explore RML Mapping TriplesMap objects  
 
        generateRDFTriples(sesameDataSet, rmlMapping, filebased);
        
	log.info("[RMLEngine:generateRDFTriples] All triples were generated ");
        
	long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.debug("[RMLEngine:runRMLMapping] RML mapping done! Generated " + sesameDataSet.getSize() + " in " + ((double) duration) / 1000000000 + "s . ");
        return sesameDataSet;
    }
    
    private boolean check_ReferencingObjectMap(RMLMapping mapping, TriplesMap map) {
        for (TriplesMap triplesMap : mapping.getTriplesMaps()) 
            for (PredicateObjectMap predicateObjectMap : triplesMap.getPredicateObjectMaps()) 
                if (predicateObjectMap.hasReferencingObjectMaps()) 
                    for (ReferencingObjectMap referencingObjectMap : predicateObjectMap.getReferencingObjectMaps()) 
                        if (referencingObjectMap.getJoinConditions().isEmpty() 
                                && referencingObjectMap.getParentTriplesMap() == map
                                && referencingObjectMap.getParentTriplesMap().getLogicalSource().getIdentifier().equals(triplesMap.getLogicalSource().getIdentifier())) 
                            return true;
        return false;
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
            RMLMapping r2rmlMapping, boolean filebased) throws SQLException, R2RMLDataError,
            UnsupportedEncodingException, ProtocolException, IOException {

        log.debug("[RMLEngine:generateRDFTriples] Generate RDF triples... ");
        int delta = 0;

        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();

        for (TriplesMap triplesMap : r2rmlMapping.getTriplesMaps()) {
            if (check_ReferencingObjectMap(r2rmlMapping, triplesMap)) 
                continue;
            //FileInputStream input = null;
            System.out.println("[RMLEngine:generateRDFTriples] Generate RDF triples for " + triplesMap.getName());
            //TODO:need to add control if reference Formulation is not defined
            //TODO:need to add check for correct spelling, rml:referenceFormulation otherwise breaks
            RMLProcessor processor = factory.create(triplesMap.getLogicalSource().getReferenceFormulation());
            
            String source = triplesMap.getLogicalSource().getIdentifier();
            InputStream input = getInputStream(source, triplesMap);
            
            processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor), input);

            log.info("[RMLEngine:generateRDFTriples] "
                    + (sesameDataSet.getSize() - delta)
                    + " triples generated for " + triplesMap.getName());
            delta = sesameDataSet.getSize();
                        
            try {
                input.close();
            } catch (IOException ex) {
                log.error("Input file could not be closed.");
                Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(filebased)
            try {
                sesameDataSet.closeRepository();
            } catch (RepositoryException ex) {
                log.error("[RMLEngine:generateRDFTriples] Cannot close output repository", ex);
            }
    }
    
    public static boolean isLocalFile(String source) {
        try {
            new URL(source);
            return false;
        } catch (MalformedURLException e) {
            return true;
        }
    }
    
    public static InputStream getInputStream (String source, TriplesMap triplesMap) throws IOException{
        InputStream input = null;
            if(!isLocalFile(source))
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL(source).openConnection();
                    con.setRequestMethod("HEAD");
                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) 
                        input = new URL(source).openStream();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
                } 
            else if(isLocalFile(source))
                try {
                    //File file  = new File(new File(source).getCanonicalPath());
                    File file  = new File(new File(source).getAbsolutePath());
                    
                    if(!file.exists()){
                        if(RMLEngine.class.getResource(triplesMap.getLogicalSource().getIdentifier()) == null){
                            source = triplesMap.getLogicalSource().getIdentifier();
                            file  = new File(new File(source).getAbsolutePath());
                        }
                        else{
                            source = RMLEngine.class.getResource(triplesMap.getLogicalSource().getIdentifier()).getFile();
                            file  = new File(new File(source).getCanonicalPath());
                        }
                        if(!file.exists())
                            log.error("[RMLEngine:generateRDFTriples] Input file not found. " );
                    }
                    input = new FileInputStream(file);
                } catch (IOException ex) {
                    Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
                } 
            else {
                log.info("Input stream was not possible.");
                return null;
            }
            return input;
    }
}
