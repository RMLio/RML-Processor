package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.performer.NodeRMLPerformer;
import be.ugent.mmlab.rml.model.dataset.FileDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.input.ConcreteLogicalSourceProcessorFactory;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
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
            LoggerFactory.getLogger(StdRMLEngine.class);
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    protected String baseIRI;
    //private static boolean source_properties;
    //Properties containing the identifiers for files
    //There are probably better ways to do this than a static variable
    private static Properties fileMap = new Properties();
    LocalRepositoryManager manager;
    
    public StdRMLEngine() {} 
    
    public StdRMLEngine(String pathToNativeStore) {
        try {
            log.debug("pathToNativeStore " + pathToNativeStore);
            File file = new File(pathToNativeStore);
            String folder = file.getParent();
            //String folder =
            //        pathToNativeStore.replaceAll("(/[a-zA-Z0-9._]*$)", "");
            log.debug("folder " + folder);
            File baseDir = new File(folder);
            manager = new LocalRepositoryManager(baseDir);
            manager.initialize();
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
    }

    public static Properties getFileMap() {
        return fileMap;
    }
    
    
    protected String getIdentifier(LogicalSource ls) {
        return StdRMLEngine.getFileMap().getProperty(ls.getSource().getTemplate());
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
    /*@Override
    public RMLDataset runRMLMapping(RMLDataset dataset, 
        RMLMapping rmlMapping, String baseIRI, 
        Map<String, String> parameters, String[] triplesMap) {
        return runRMLMapping( dataset,
                rmlMapping, baseIRI, parameters, triplesMap);
    }*/

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
                log.debug("dataset is generated");
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
            RMLProcessor processor = generateRMLProcessor(triplesMap);
            
            if (processor != null) {
                log.info("Generating Data Retrieval Processor..");
                inputProcessor =
                        generateInputProcessor(triplesMap, parameters);
                do {
                    dataset = processInputStream(processor, inputProcessor,
                            triplesMap, parameters, exeTriplesMap, dataset);
                } while (inputProcessor.hasNextInputStream());
            }         
                    
            /*try {
                log.info((dataset.getSize() - delta)
                        + " triples were generated for " 
                        + triplesMap.getName());
                //TODO: Add metadata that this Triples Map generatedthat many triples
                //delta = dataset.getSize();
            } catch (Exception ex) {
                log.error("Exception " + ex);
                log.error("The execution of the mapping failed.");
            }*/
        }
        return dataset;
    }
    
    @Override
    public RMLProcessor generateRMLProcessor(TriplesMap triplesMap) {
        RMLProcessor processor = null;
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        if(triplesMap.getLogicalSource() == null){
            log.error(triplesMap.getName() + " Logical Source: " 
                    + triplesMap.getLogicalSource());
            return null;
        }
        else
            log.debug("Logical Source: " 
                    + triplesMap.getLogicalSource());
        log.debug("Reference formulation: " 
                    + triplesMap.getLogicalSource().getReferenceFormulation());
        try {
            processor = factory.create(
                    triplesMap.getLogicalSource().getReferenceFormulation());
        } catch (Exception ex) {
            log.error("Exception " + ex + 
                    " There is no suitable processor for this reference formulation");
        }

        return processor;
    }
    
    //TODO: Check if it's needed here or if I should take it to the DataRetrieval
    protected SourceProcessor generateInputProcessor(
            TriplesMap triplesMap, Map<String, String> parameters) {
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