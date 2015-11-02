package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
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
    private MetadataGenerator metadataGenerator;
    private String pathToNativeStore;
    
    public RMLEngineMeta(String pathToNativeStore, String outputFormat){
        try {
            this.pathToNativeStore = pathToNativeStore;
            //generate repository manager
            //TODO: Replace the following with OS temp folder?
            String folder = 
                    pathToNativeStore.replaceAll("(/[a-zA-Z0-9._]*$)", "");
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
            Map<String, String> parameters, String[] exeTriplesMap, String mdl) {
        
        long startTime = System.nanoTime();
        RMLDataset dataset ;
        
        dataset = super.runRMLMapping(rmlMapping, baseIRI, pathToNativeStore, 
                outputFormat, parameters, exeTriplesMap, mdl);
        
        log.debug("Generating Dataset metadata..");
        metadataGenerator.generateMetaData(
                outputFormat, dataset, pathToNativeStore, startTime);
        dataset.closeRepository();
        metadataDataset.closeRepository();
            
        return dataset;      
    }
    
    @Override
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset dataset) {

        log.debug("Generating Triples Map triples with metadata");
        dataset = super.generateTriplesMapTriples(triplesMap, parameters,
                exeTriplesMap, dataset);
        
        log.debug("Generating Triples Map metadata..");
        metadataGenerator.generateTriplesMapMetaData(
                dataset, metadataDataset, triplesMap, pathToNativeStore);

        return dataset;
    }
    
}
