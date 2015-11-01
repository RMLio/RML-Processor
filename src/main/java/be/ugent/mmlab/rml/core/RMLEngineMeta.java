package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.io.File;
import java.util.Map;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class RMLEngineMeta extends StdRMLEngine implements RMLEngine {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(RMLEngineMeta.class);
    
    private RMLDataset metadataDataset;
    private Integer datasetSize = 0;
    private Integer totaldistinctSubjects = 0;
    
    MetadataGenerator metadataGenerator;
    
    public RMLEngineMeta(String pathToNativeStore, String outputFormat){
        try {
            String folder = 
                    pathToNativeStore.replaceAll("(/[a-zA-Z0-9.]*$)", "");
            log.debug("folder " + folder);
            File baseDir = new File(folder);
            manager = new LocalRepositoryManager(baseDir);
            manager.initialize();
            String repositoryID = "metadata";

            //generate the file that contains the metadata graph
            String pathToMetadataStore = 
                    pathToNativeStore.replaceAll("(\\.[a-zA-Z0-9]*$)", ".metadata$1");
            log.debug("Metadata dataset is generated at " + pathToMetadataStore);
            
            //generate dataset for the metadata graph
            metadataDataset = 
                    chooseSesameDataSet(
                    repositoryID, pathToMetadataStore, outputFormat);
            
            metadataGenerator =
                    new MetadataGenerator(metadataDataset, pathToNativeStore);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } 
    }
    
    public RMLDataset getMetadataDataset(){
        return this.metadataDataset;
    }
    
    @Override
    public RMLDataset runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore, String outputFormat, 
            Map<String, String> parameters, String[] exeTriplesMap, String prov) {
        
        long startTime = System.nanoTime();
        RMLDataset dataset ;

        log.debug("Running RML mapping... ");
        if (rmlMapping == null) 
            log.info("No RML Mapping object found.");
        if (baseIRI == null) 
            log.info("No base IRI found.");
        
        dataset = chooseSesameDataSet("dataset", pathToNativeStore, outputFormat);
        log.debug("Dataset repository generated");
        // Update baseIRI
        this.baseIRI = baseIRI;
        
        log.debug("Generating triples..");
        dataset = generateRDFTriples(
                dataset, rmlMapping, parameters, exeTriplesMap);
        
        log.debug("Generating metadata..");
        //TODO:improve/replace metadata generator
        metadataGenerator.generateMetaData(
                outputFormat, dataset, startTime, totaldistinctSubjects);
        dataset.closeRepository();
        metadataDataset.closeRepository();
            
        return dataset;
    }
    
    @Override
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset) {
        boolean flag = true;
        int delta = 0;
        
        log.debug("Generating Triples Map triples with metadata");
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

            log.info("Generating Data Retrieval Processor..");
            SourceProcessor inputProcessor = 
                    generateInputProcessor(triplesMap, parameters);
           
            do {
                dataset = processInputStream(processor, inputProcessor,
                        triplesMap, parameters, exeTriplesMap, dataset);
            } while (inputProcessor.hasNextInputStream());
            
            //Calculating Triples Map size
            int triplesMapSize = dataset.getSize() - datasetSize;
            datasetSize = dataset.getSize();
            Integer entities = processor.getDistinctSubjects();
            totaldistinctSubjects = totaldistinctSubjects + entities;
            
            metadataGenerator.generateTriplesMapMetaData(
                    metadataDataset, triplesMap, triplesMapSize, entities);
            
            try {
                log.info((dataset.getSize() - delta)
                        + " triples were generated for " 
                        + triplesMap.getName());
            } catch (Exception ex) {
                log.error("Exception " + ex);
                log.error("The execution of the mapping failed.");
            }
        }
        return dataset;
    }
    
}
