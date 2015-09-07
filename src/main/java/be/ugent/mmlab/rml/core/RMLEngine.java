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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
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
    public RMLSesameDataSet runRMLMapping(
            RMLMapping rmlMapping, String baseIRI, 
            Map<String, String> parameters, String[] triplesMap) 
            throws SQLException, UnsupportedEncodingException, IOException {
        return runRMLMapping(
                rmlMapping, baseIRI, null, "ntriples", parameters, triplesMap);
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
            Map<String, String> parameters, String[] exeTriplesMap) {
        long startTime = System.nanoTime();
        RMLSesameDataSet sesameDataSet = null;

        log.debug("Running RML mapping... ");
        if (rmlMapping == null) 
            log.info("No RML Mapping object found.");
        if (baseIRI == null) 
            log.info("No base IRI found.");
        
        sesameDataSet = chooseSesameDataSet(pathToNativeStore, outputFormat);
        log.debug("Dataset repository generated");
        // Update baseIRI
        this.baseIRI = baseIRI;
        
        log.debug("Generating triples..");
        sesameDataSet = generateRDFTriples(
                sesameDataSet, rmlMapping, parameters, exeTriplesMap);
        
        log.debug("Generating metadata..");
        //TODO:improve/replace metadata generator
        generateMetaData(sesameDataSet, startTime);
            
        return sesameDataSet;
    }
    
    private RMLSesameDataSet chooseSesameDataSet(
            String pathToNativeStore, String outputFormat){
        RMLSesameDataSet sesameDataSet;
        if (pathToNativeStore != null) {
            log.info("Using direct file " + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore, outputFormat);
        } else {
            log.info("Using default store (memory) ");
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
    private RMLSesameDataSet generateRDFTriples(
            RMLSesameDataSet sesameDataSet, RMLMapping rmlMapping, 
            Map<String, String> parameters, String[] exeTriplesMap) {

        log.debug("Generate RDF triples... ");
        Collection<TriplesMap> triplesMaps;
        
        if(exeTriplesMap != null && exeTriplesMap.length != 0)
            triplesMaps = processExecutionList(rmlMapping, exeTriplesMap);
        else
            triplesMaps = rmlMapping.getTriplesMaps();

        for (TriplesMap triplesMap : triplesMaps) {
            sesameDataSet = generateTriplesMapTriples(triplesMap, parameters, 
                    exeTriplesMap, sesameDataSet);
        }

        return sesameDataSet;
    }
    
    private RMLSesameDataSet generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLSesameDataSet sesameDataSet) {
        boolean flag = true;
        int delta = 0;

        if (exeTriplesMap != null) {
            flag = checkExecutionList(triplesMap, exeTriplesMap);
        }
        if (flag) {
            System.out.println("Generating RDF triples for " 
                    + triplesMap.getName());
            //TODO: Add metadata that this Map Doc has that many Triples Maps

            log.info("Generating RML Processor..");
            RMLProcessor processor = generateRMLProcessor(triplesMap);

            log.info("Generating Data Retrieval Processor..");
            InputStream input = generateInputStream(triplesMap, parameters);

            try {
                log.debug("Generating Performer..");
                NodeRMLPerformer performer = new NodeRMLPerformer(processor);

                log.debug("Executing Mapping Processor..");
                processor.execute(
                        sesameDataSet, triplesMap, performer, input, false);

                log.info((sesameDataSet.getSize() - delta)
                        + " triples were generated for " 
                        + triplesMap.getName());
                //TODO: Add metadata that this Triples Map generatedthat many triples
                delta = sesameDataSet.getSize();
            } catch (Exception ex) {
                log.error("Exception " + ex);
                log.error("The execution of the mapping failed.");
            }

            try {
                input.close();
            } catch (IOException ex) {
                log.error("IOException " + ex);
            }
        }
        return sesameDataSet;
    }
    
    private boolean checkExecutionList(TriplesMap triplesMap, String[] exeTriplesMap) {
        boolean flag = false;

        for (String exeTM : exeTriplesMap) {
            flag = false;

            if (triplesMap.getName().toString().equals(exeTM.toString())) {
                flag = true;
            }
        }

        return flag;
    }
    
    private Collection<TriplesMap> processExecutionList(
            RMLMapping rmlMapping, String[] exeTriplesMap) {
        Collection<TriplesMap> tms = rmlMapping.getTriplesMaps();
        Collection<TriplesMap> triplesMaps = new LinkedHashSet<TriplesMap>();

        for (String exeTM : exeTriplesMap) {
            for (TriplesMap tm : tms) {
                if (tm.getName().equals(exeTM.toString())) {
                    triplesMaps.add(tm);
                }
            }
        }

        return triplesMaps;
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
            log.error("Exception " + ex + 
                    " There is no suitable processor for this reference formulation");
        }

        return processor;
    }
    
    //TODO: Check if it's needed here or if I should take it to the DataRetrieval
    private InputStream generateInputStream(
            TriplesMap triplesMap, Map<String, String> parameters) {
        ConcreteLogicalSourceProcessorFactory logicalSourceProcessorFactory = 
                new ConcreteLogicalSourceProcessorFactory();
        
        SourceProcessor inputProcessor = 
                logicalSourceProcessorFactory.
                createSourceProcessor(triplesMap.getLogicalSource().getSource());

        InputStream input = inputProcessor.getInputStream(
                triplesMap.getLogicalSource(), parameters);
        return input;
    }
    
    private void generateMetaData(
            RMLSesameDataSet sesameDataSet, long startTime) {
        //TODO:add metadata this Triples Map started then, finished then and lasted that much
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info("RML mapping done! Generated "
                + sesameDataSet.getSize() + " in "
                + ((double) duration) / 1000000000 + "s . ");
    }
}