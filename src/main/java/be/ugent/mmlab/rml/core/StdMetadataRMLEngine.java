package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataFileDataset;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class StdMetadataRMLEngine extends StdRMLEngine implements RMLEngine {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(StdMetadataRMLEngine.class);

    private MetadataGenerator metadataGenerator;
    private String pathToNativeStore;
    
    public StdMetadataRMLEngine(String pathToNativeStore){
        try {
            this.pathToNativeStore = pathToNativeStore;
            //generate repository manager
            //TODO: Replace the following with OS temp folder?
            String folder = 
                    pathToNativeStore.replaceAll("(/[a-zA-Z0-9._]*$)", "");
            File baseDir = new File(folder);
            manager = new LocalRepositoryManager(baseDir);
            manager.initialize();
            
            metadataGenerator =
                    new MetadataGenerator(pathToNativeStore);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } 
    }
    
    /*public RMLDataset getMetadataDataset(){
        return this.metadataDataset;
    }*/
    
    @Override
    public RMLDataset runRMLMapping(RMLDataset originalDataset, RMLMapping rmlMapping,
            String baseIRI, Map<String, String> parameters, String[] exeTriplesMap) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd h:mm:ss");
        String startDateTime = sdf.format(new Date()).replace(' ', 'T') + 'Z';
        
        dataset = (MetadataRMLDataset) super.runRMLMapping(
                dataset, rmlMapping, baseIRI, parameters, exeTriplesMap);
        
        String endDateTime = sdf.format(new Date()).replace(' ', 'T') + 'Z';
        
        log.debug("Generating Dataset metadata..");
        metadataGenerator.generateMetaData(rmlMapping,
                dataset, pathToNativeStore, 
                startDateTime, endDateTime);
        
        dataset.getMetadataDataset().closeRepository();
        
        dataset.closeRepository();
        
        return dataset;      
    }
    
    @Override
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset originalDataset) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
        
        log.debug("Generating Triples Map triples with metadata");
        //TODO:Check if it corrects generates ISO 8601 date/time string for xsd
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd h:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String startDateTime = sdf.format(new Date());//.replace(' ', 'T') + 'Z';
        
        dataset = (MetadataRMLDataset) super.generateTriplesMapTriples(
                triplesMap, parameters, exeTriplesMap, dataset);
        
        //sdf = new SimpleDateFormat("yyyy-mm-dd h:mm:ss");
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String endDateTime = sdf.format(new Date());//.replace(' ', 'T') + 'Z';
        
        if(dataset.getMetadataLevel().equals("triplesmap") 
                || dataset.getMetadataLevel().equals("triple"))
            metadataGenerator.generateTriplesMapMetaData(dataset, triplesMap, 
                    pathToNativeStore, startDateTime, endDateTime);

        return dataset;
    }
    
    @Override
    public RMLDataset chooseSesameDataSet(String repositoryID,
            String pathToNativeStore, String outputFormat){

            RMLDataset dataset;
            
            if (pathToNativeStore != null) {
                log.debug("Using direct file " + pathToNativeStore);
                dataset = new MetadataFileDataset(pathToNativeStore, outputFormat, 
                        manager, repositoryID);
                log.debug("Dataset is generated");
            } else {
                log.debug("Using default store (memory) ");
                dataset = new StdRMLDataset();
            }
                    
        return dataset;
    }
                         
}
