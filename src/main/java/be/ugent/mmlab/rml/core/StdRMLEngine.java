package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.performer.NodeRMLPerformer;
import be.ugent.mmlab.rml.model.dataset.FileDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.input.ConcreteLogicalSourceProcessorFactory;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * Engine that will perform the mapping starting from the TermMaps
 * 
 * @author mielvandersande, andimou
 */
public class StdRMLEngine implements RMLEngine {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(StdRMLEngine.class.getSimpleName());
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    protected String baseIRI;
    //private static boolean source_properties;
    //Properties containing the identifiers for files
    //There are probably better ways to do this than a static variable
    LocalRepositoryManager manager;
    protected Map<String,Integer> enumerator = new HashMap<String,Integer>();
    
    public StdRMLEngine() {} 
    
    public StdRMLEngine(String pathToNativeStore) {
        try {
            File file = new File(pathToNativeStore);
            String folder = file.getParent();
            File baseDir = new File(folder);
            manager = new LocalRepositoryManager(baseDir);
            manager.initialize();
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
    }
    
    
    @Override
    public void run(RMLMapping mapping, String outputFile, String outputFormat, 
            String graphName, Map<String,String> parameters, String[] exeTriplesMap,
            String metadataLevel, String metadataFormat, String metadataVocab) {
        RMLDataset dataset;
        log.debug("Running without metadata...");

        //RML Engine that does not generate metadata
        dataset = chooseSesameDataSet(
                "dataset", outputFile, outputFormat);

        runRMLMapping(dataset, mapping, graphName, parameters, exeTriplesMap);
        
        dataset.closeRepository();
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
    @Override
    public RMLDataset runRMLMapping(RMLDataset dataset, RMLMapping rmlMapping, 
        String baseIRI, Map<String, String> parameters, String[] exeTriplesMap) {
        long startTime = System.nanoTime();
        
        log.debug("Running RML mapping... ");
        if (rmlMapping == null) 
            log.info("No RML Mapping object found.");
        if (baseIRI == null) 
            log.info("No base IRI found.");
        
        log.debug("Dataset repository generated");
        // Update baseIRI
        this.baseIRI = baseIRI;
        
        log.debug("Generating triples..");
        dataset = generateRDFTriples(
                dataset, rmlMapping, parameters, exeTriplesMap);
        
        //log.debug("Generating dataset metadata..");
        generateBasicMetadataInfo(dataset, startTime);
        
        return dataset;
    }
    
    @Override
    public RMLDataset chooseSesameDataSet(String repositoryID,
            String pathToNativeStore, String outputFormat){

            RMLDataset dataset;
            
            if (pathToNativeStore != null) {
                log.debug("Using direct file " + pathToNativeStore);
                dataset = new FileDataset(pathToNativeStore, outputFormat, 
                        manager, repositoryID);
                log.debug("Dataset is generated");
            } else {
                log.debug("Using default store (memory) ");
                dataset = new StdRMLDataset();
            }
                    
        return dataset;
    }

    /**
     * This process adds RDF triples to the output dataset. Each generated
     * triple is placed into one or more graphs of the output dataset. The
     * generated RDF triples are determined by the following algorithm.
     *
     * @param sesameDataSet
     * @param rmlMapping
     */
    protected RMLDataset generateRDFTriples(
            RMLDataset dataset, RMLMapping rmlMapping, 
            Map<String, String> parameters, String[] exeTriplesMap) {

        log.debug("Generate RDF triples... ");
        Collection<TriplesMap> triplesMaps;
        RMLExecutionEngine executionEngine = 
                new RMLExecutionEngine(exeTriplesMap);
        
        log.debug("Generating execution list... ");
        if(exeTriplesMap != null && exeTriplesMap.length != 0){
            triplesMaps = executionEngine.
                    processExecutionList(rmlMapping, exeTriplesMap);
        }
        else
            triplesMaps = rmlMapping.getTriplesMaps();

        for (TriplesMap triplesMap : triplesMaps) {
            dataset = this.generateTriplesMapTriples(
                    triplesMap, parameters, exeTriplesMap, dataset);
        }

        return dataset;
    }
    
    @Override
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset) {
        boolean flag = true;
        SourceProcessor inputProcessor;

        if (exeTriplesMap != null) {
            RMLExecutionEngine executionEngine = 
                new RMLExecutionEngine(exeTriplesMap);
            flag = executionEngine.
                    checkExecutionList(triplesMap, exeTriplesMap);
        }
        if (flag) {
            System.out.println("Generating RDF triples for " 
                    + triplesMap.getName());
            //TODO: Add metadata that this Map Doc has that many Triples Maps

            log.info("Generating RML Processor..");
            RMLProcessor processor = generateRMLProcessor(triplesMap, parameters);
            
            if (processor != null) {
                log.info("Generating Data Retrieval Processor..");
                inputProcessor =
                        generateInputProcessor(triplesMap);
                do {
                    dataset = processInputStream(processor, inputProcessor,
                            triplesMap, parameters, exeTriplesMap, dataset);
                } while (inputProcessor.hasNextInputStream());
            } 
        }
        return dataset;
    }
    
    @Override
    public RMLProcessor generateRMLProcessor(
            TriplesMap triplesMap, Map<String, String> parameters) {
        RMLProcessor processor = null;
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        if(triplesMap.getLogicalSource() == null){
            log.error(triplesMap.getName() + " Logical Source: " 
                    + triplesMap.getLogicalSource());
            return null;
        }

        try {
            processor = factory.create(
                    triplesMap.getLogicalSource().getReferenceFormulation(),
                    parameters, triplesMap);
        } catch (Exception ex) {
            log.error("Exception " + ex + 
                    " There is no suitable processor for this reference formulation");
        }

        return processor;
    }
    
    //TODO: Check if it's needed here or if I should take it to the DataRetrieval
    protected SourceProcessor generateInputProcessor(
            TriplesMap triplesMap) {
        ConcreteLogicalSourceProcessorFactory logicalSourceProcessorFactory = 
                new ConcreteLogicalSourceProcessorFactory();
        
        SourceProcessor inputProcessor = 
                logicalSourceProcessorFactory.
                createSourceProcessor(triplesMap.getLogicalSource().getSource());

        return inputProcessor;
    }
    
    protected RMLDataset processInputStream(
            RMLProcessor processor, SourceProcessor inputProcessor, 
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset) {
        InputStream input = null;
        
        log.debug("Generating Input Stream..");
        try {
            input = inputProcessor.getInputStream(
                    triplesMap.getLogicalSource(), parameters);
        } catch (Exception ex) {
            log.error("Exception ex " + ex);
            log.error("Input stream was not properly retrieved.");
        }

        try {
            if (input != null) {
                log.debug("Generating Performer..");
                NodeRMLPerformer performer =
                        new NodeRMLPerformer(processor);

                log.debug("Executing Mapping Processor..");
                processor.execute(dataset, triplesMap, performer,
                        input, exeTriplesMap, false);
                Integer iteration = processor.getEnumerator();
                enumerator.put(triplesMap.getShortName(), iteration);
            }
            else{
                log.debug("Null input data derived from " + 
                        triplesMap.getLogicalSource().getSource().getTemplate());
                return dataset;
            }
            
        } catch (Exception ex) {
            log.error("Exception " + ex);
            log.error("The execution of the mapping failed.");
        }
        
        try {
            input.close();
        } catch (IOException ex) {
            log.error("IOException " + ex);
        }  
        return dataset;
    }
    
    private void generateBasicMetadataInfo(
            RMLDataset sesameDataSet, long startTime) {
        //TODO:add metadata this Triples Map started then, finished then and lasted that much
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info("RML mapping done! Generated "
                + sesameDataSet.getSize() + " in "
                + ((double) duration) / 1000000000 + "s . ");
    }
}