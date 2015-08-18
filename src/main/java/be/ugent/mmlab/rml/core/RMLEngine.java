package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.dataset.FileSesameDataset;
import be.ugent.mmlab.rml.input.ConcreteLogicalSourceProcessorFactory;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * Engine that will perform the mapping starting from the TermMaps
 * 
 * @author mielvandersande, andimou
 */
public class RMLEngine {

    // Log
    private static final Logger log = LoggerFactory.getLogger(RMLEngine.class);
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
        return RMLEngine.getFileMap().getProperty(ls.getSource().getTemplate());
    }

    /**
     * Generate RDF based on a RML mapping
     *
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @return dataset containing the triples
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public RMLSesameDataSet runRMLMapping(RMLMapping rmlMapping, String baseIRI, String[] triplesMap) 
            throws SQLException, UnsupportedEncodingException, IOException {
        return runRMLMapping(rmlMapping, baseIRI, null, "ntriples", null, triplesMap);
    }

    /**
     *
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @param pathToNativeStore path if triples have to be stored in sesame
     * triple store instead of memory
     * @return RMLSesameDataSet
     * 
     */
    public RMLSesameDataSet runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore, String outputFormat, 
            String parameter, String[] exeTriplesMap) {
        long startTime = System.nanoTime();

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "[RMLEngine:runRMLMapping] Run RML mapping... ");
        if (rmlMapping == null) 
            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "No RML Mapping object found.");
        if (baseIRI == null) 
            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "[RMLEngine:runRMLMapping] No base IRI found.");

        RMLSesameDataSet sesameDataSet = chooseSesameDataSet(pathToNativeStore, outputFormat);
        // Update baseIRI
        this.baseIRI = baseIRI;

        sesameDataSet = generateRDFTriples(sesameDataSet, rmlMapping, exeTriplesMap);
               
        //TODO:add metadata this Triples Map started then, finished then and lasted that much
	long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "RML mapping done! Generated " 
                + sesameDataSet.getSize() + " in " 
                + ((double) duration) / 1000000000 + "s . ");

        return sesameDataSet;
    }
    
    private RMLSesameDataSet chooseSesameDataSet(
            String pathToNativeStore, String outputFormat){
        RMLSesameDataSet sesameDataSet;
        if (pathToNativeStore != null) {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use direct file "
                    + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore, outputFormat);
        } else {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use default store (memory) ");
            sesameDataSet = new RMLSesameDataSet();
        }
        return sesameDataSet;
        
    }

    /**
     * This process adds RDF triples to the output dataset. Each generated
     * triple is placed into one or more graphs of the output dataset. The
     * generated RDF triples are determined by the following algorithm.
     *
     * @param sesameDataSet
     * @param rmlMapping
     */
    private RMLSesameDataSet generateRDFTriples(RMLSesameDataSet sesameDataSet,
            RMLMapping rmlMapping, String[] exeTriplesMap) {

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Generate RDF triples... ");

        for (TriplesMap triplesMap : rmlMapping.getTriplesMaps()) {
            sesameDataSet = generateTriplesMapTriples(triplesMap, exeTriplesMap, sesameDataSet);
        }
        //log.info("sesameDataSet " + sesameDataSet.printRDF(RDFFormat.TURTLE));
        /*try {
            sesameDataSet.closeRepository();
        } catch (RepositoryException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Cannot close output repository", ex);
        }*/
        return sesameDataSet;
    }
    
    private RMLSesameDataSet generateTriplesMapTriples(
            TriplesMap triplesMap, String[] exeTriplesMap, RMLSesameDataSet sesameDataSet) {
        int delta = 0;

        /*if (exeTriplesMap != null) {
            boolean flag = false;
            for (String exeTM : exeTriplesMap) {
                if (triplesMap.getName().toString().equals(exeTM.toString())) {
                    flag = true;
                }
            }
            if (!flag) {
                log.error("not to be executed");
                return null;
            }
        }*/

        System.out.println("Generating RDF triples for " + triplesMap.getName());
        //TODO: Add metadata that this Map Doc has that many Triples Maps

        log.info("Generating RML Processor..");
        RMLProcessor processor = generateRMLProcessor(triplesMap);
        
        log.info("Generating Input Processor..");
        InputStream input = generateInputStream(triplesMap);

        try {
            processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor), input);

            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + (sesameDataSet.getSize() - delta)
                    + " triples were generated for " + triplesMap.getName());
            //TODO: Add metadata that this Triples Map generatedthat many triples
            delta = sesameDataSet.getSize();
        } catch (Exception ex) {
            log.error("Exception " + ex);
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "The execution of the mapping failed.");
        }

        try {
            input.close();
        } catch (IOException ex) {
            log.error("IOException " + ex);
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Input file could not be closed.");
        }
        return sesameDataSet;
    }
    
    private RMLProcessor generateRMLProcessor(TriplesMap triplesMap) {
        RMLProcessor processor = null;
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        log.debug("Logical Source: " 
                    + triplesMap.getLogicalSource());
        log.debug("Reference formulation: " 
                    + triplesMap.getLogicalSource().getReferenceFormulation());
        try {
            processor = factory.create(triplesMap.getLogicalSource().getReferenceFormulation());
        } catch (Exception ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex
                    + " There is no suitable processor for this reference formulation");
        }

        return processor;
    }
    
    //TODO: Check if it's needed here or if I should take it to the DataRetrieval
    private InputStream generateInputStream(TriplesMap triplesMap) {
        ConcreteLogicalSourceProcessorFactory logicalSourceProcessorFactory = 
                new ConcreteLogicalSourceProcessorFactory();
        //SourceProcessor inputProcessor = new AbstractInputProcessor();
        SourceProcessor inputProcessor = 
                logicalSourceProcessorFactory.
                createSourceProcessor(triplesMap.getLogicalSource().getSource());

        String source = triplesMap.getLogicalSource().getSource().getTemplate();

        InputStream input = inputProcessor.
                getInputStream(triplesMap.getLogicalSource().getSource());
        return input;
    }
}