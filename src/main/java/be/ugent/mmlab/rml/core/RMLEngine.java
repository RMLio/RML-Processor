package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.extractor.input.InputExtractor;
import be.ugent.mmlab.rml.extractor.input.concrete.LocalFileExtractor;
import be.ugent.mmlab.rml.dataset.FileSesameDataset;
import be.ugent.mmlab.rml.extractor.input.concrete.ApiExtractor;
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
import java.util.Set;
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
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping, String baseIRI, String[] triplesMap) 
            throws SQLException, UnsupportedEncodingException, IOException {
        return runRMLMapping(rmlMapping, baseIRI, null, "ntriples", null, triplesMap, false);
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
            String baseIRI, String pathToNativeStore, String outputFormat, 
            String parameter,String[] exeTriplesMap, boolean filebased) 
            throws SQLException, UnsupportedEncodingException, IOException {
        long startTime = System.nanoTime();

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "[RMLEngine:runRMLMapping] Run RML mapping... ");
        if (rmlMapping == null) 
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No RML Mapping object found.");
        if (baseIRI == null) 
            throw new IllegalArgumentException(
                    "[RMLEngine:runRMLMapping] No base IRI found.");

        SesameDataSet sesameDataSet = chooseSesameDataSet(pathToNativeStore, outputFormat, filebased);
        // Update baseIRI
        this.baseIRI = baseIRI;
        
        // Explore RML Mapping TriplesMap objects  
 
        generateRDFTriples(sesameDataSet, rmlMapping, parameter, exeTriplesMap, filebased);
               
        //TODO:add metadata this Triples Map started then, finished then and lasted that much
	long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "RML mapping done! Generated " 
                + sesameDataSet.getSize() + " in " 
                + ((double) duration) / 1000000000 + "s . ");

        return sesameDataSet;
    }
    
    private SesameDataSet chooseSesameDataSet(
            String pathToNativeStore, String outputFormat, boolean filebased){
        SesameDataSet sesameDataSet;
        if (filebased) {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use direct file "
                    + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore, outputFormat);
        } else if (pathToNativeStore != null) { // Check if use of native store is required
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use native store "
                    + pathToNativeStore);
            sesameDataSet = new SesameDataSet(pathToNativeStore, false);
        } else {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use default store (memory) ");
            sesameDataSet = new RMLSesameDataSet();
        }
        return sesameDataSet;
        
    }
    
    private boolean check_ReferencingObjectMap(RMLMapping mapping, TriplesMap map, String[] exeTriplesMap) {
        for (TriplesMap triplesMap : mapping.getTriplesMaps()) 
            for (PredicateObjectMap predicateObjectMap : triplesMap.getPredicateObjectMaps()) 
                if (predicateObjectMap.hasReferencingObjectMaps()) 
                    for (ReferencingObjectMap referencingObjectMap : predicateObjectMap.getReferencingObjectMaps()) 
                        if (!referencingObjectMap.getJoinConditions().isEmpty() 
                                && referencingObjectMap.getParentTriplesMap().getName().equals(map.getName())
                                && exeTriplesMap != null)
                                //&& referencingObjectMap.getParentTriplesMap().getLogicalSource().getIdentifier().equals(triplesMap.getLogicalSource().getIdentifier())) 
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
            RMLMapping rmlMapping, String parameter, String[] exeTriplesMap, boolean filebased) 
            throws SQLException, UnsupportedEncodingException, ProtocolException, IOException {

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Generate RDF triples... ");
        int delta = 0;
        
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        outerloop:
        for (TriplesMap triplesMap : rmlMapping.getTriplesMaps()) {
            if(exeTriplesMap != null){
                boolean flag = false;
                for(String exeTM : exeTriplesMap){
                    if(triplesMap.getName().toString().equals(exeTM.toString())){
                        flag = true;
                    }
                    //if (check_ReferencingObjectMap(rmlMapping, triplesMap, exeTriplesMap))
                        //continue;
                }
                if(!flag)
                    continue;
            }
            
            System.out.println("Generating RDF triples for " + triplesMap.getName());
            //TODO: Add metadata that this Map Doc has that many Triples Maps
            
            RMLProcessor processor = null;
            try {
                processor = factory.create(triplesMap.getLogicalSource().getReferenceFormulation());
            } catch (Exception ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "There is no suitable processor for this reference formulation");
            }
            
            String source = triplesMap.getLogicalSource().getIdentifier();
            
            InputExtractor inputExtractor = null;
            if (isLocalFile(source)) {
                inputExtractor = new LocalFileExtractor();
            } else if (!isLocalFile(source)) {
                inputExtractor = new ApiExtractor();
            } else {
                log.info("Input stream was not identified.");
            }
            
            String[] splitParameter = null;
            if (parameter != null) {
                splitParameter = parameter.split("=");
            }
            Set<String> variables = inputExtractor.extractStringTemplate(source);
            if (!variables.isEmpty()) {
                source = source.replaceAll("\\{" + variables.iterator().next() + "\\}", splitParameter[1]);
            }
            InputStream input = inputExtractor.getInputStream(source, triplesMap);
            
            try {
                processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor), input);
            } catch (Exception ex) {
                log.error(ex);
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "The execution of the mapping failed.");
            }

            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + (sesameDataSet.getSize() - delta)
                    + " triples were generated for " + triplesMap.getName());
            //TODO: Add metadata that this Triples Map generatedthat many triples
            delta = sesameDataSet.getSize();
                        
            try {
                input.close();
            } catch (IOException ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Input file could not be closed.");
            }
        }
        if(filebased)
            try {
                sesameDataSet.closeRepository();
            } catch (RepositoryException ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Cannot close output repository", ex);
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
                            log.error(
                                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                    + "Input file not found. " );
                    }
                    input = new FileInputStream(file);
                } catch (IOException ex) {
                    Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
                } 
            else {
                log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Input stream was not possible.");
                return null;
            }
            return input;
    }
}
