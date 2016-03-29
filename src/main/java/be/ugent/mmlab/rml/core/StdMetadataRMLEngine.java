package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataFileDataset;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class StdMetadataRMLEngine extends StdRMLEngine {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(
            StdMetadataRMLEngine.class.getSimpleName());

    private MetadataGenerator metadataGenerator;
    private String pathToNativeStore;
    private RDFFormat format = RDFFormat.TURTLE;
    SailRepositoryConfig repositoryTypeSpec;
    
    public StdMetadataRMLEngine(String pathToNativeStore) {
        super(pathToNativeStore);
        this.pathToNativeStore = pathToNativeStore;
        this.metadataGenerator =
                new MetadataGenerator(pathToNativeStore, manager);
        String indexes = "spoc";
        repositoryTypeSpec = 
                    new SailRepositoryConfig(new NativeStoreConfig(indexes));
    }
    
    @Override
    public void run(
            RMLMapping mapping, String outputFile, String outputFormat,
            String graphName, Map<String, String> parameters, String[] exeTriplesMap, 
            String metadataLevel, String metadataFormat, String metadataVocab) {
        //StdMetadataRMLEngine engine; 
        MetadataRMLDataset dataset;
        
        //If not user-defined, use same as for the output
        if (metadataFormat == null) {
            metadataFormat = outputFormat;
        }
        
        //RML Engine that generates metadata too
        //engine = new StdMetadataRMLEngine(outputFile);

        //generate the repository ID for the metadata graph
        String metadataRepositoryID = "metadata";
        //Generate the repository for the metadata graph
        generateRepository(metadataRepositoryID);
        //generate the repository ID for the metadata graph
        String datasetRepositoryID = generateRepositoryIDFromFile(outputFile);
        //Generate the repository for the actual dataset
        generateRepository(datasetRepositoryID);

        //Generate dataset for the actual dataset graph
        dataset = (MetadataRMLDataset) chooseSesameDataSet(
                    datasetRepositoryID, outputFile, outputFormat);
        //Set dataset metadata
        dataset.setDatasetMetadata(metadataLevel, metadataFormat, metadataVocab);

        runRMLMapping(dataset, mapping, graphName, parameters, exeTriplesMap);
        
        File file = new File(dataset.getRepository().getDataDir().getParent());
        boolean out = file.delete(); 
        
    }
    
    @Override
    public RMLDataset runRMLMapping(RMLDataset originalDataset, RMLMapping rmlMapping,
            String baseIRI, Map<String, String> parameters, String[] exeTriplesMap) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd h:mm:ss");
        String startDateTime = sdf.format(new Date()).replace(' ', 'T') + 'Z';
        
        dataset = (MetadataRMLDataset) super.runRMLMapping(
                dataset, rmlMapping, baseIRI, parameters, exeTriplesMap);
        
        String endDateTime = sdf.format(new Date()).replace(' ', 'T') + 'Z';
        
        dataset = metadataGenerator.generateMetaData(rmlMapping,
                dataset, pathToNativeStore, 
                startDateTime, endDateTime);

        //generate the file that contains the metadata graph
        String pathToMetadataStore =
                dataset.getTarget().toString().replaceAll(
                "(\\.[a-zA-Z0-9]*$)", "_metadata$1");
        try {
            dataset.setRepository(manager.getRepository("metadata"));
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        removeRepository(dataset, pathToMetadataStore);
        
        if (dataset.getMetadataLevel().equals("triplesmap") || 
                dataset.getMetadataLevel().equals("triple") //|| 
                //dataset.getMetadataVocab().contains("co")
                ) {
            log.debug("Writing metadata...");
            Collection<TriplesMap> triplesMaps = rmlMapping.getTriplesMaps();
            writeRepositories(dataset, triplesMaps, dataset.getTarget().toString());
        }
        else {
            try {
                dataset.setRepository(manager.getRepository(dataset.getID()));
            } catch (RepositoryConfigException ex) {
                log.error("Repository Config Exception " + ex);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
            removeRepository(dataset, dataset.getTarget().toString());
        }

        try {
            Set<String> ids = manager.getRepositoryIDs();
            for (String id : ids) {
                if (!id.equals("SYSTEM")) {
                    manager.getRepository(id).shutDown();
                    manager.removeRepository(id);
                }
            }
            manager.getSystemRepository().shutDown();
            manager.shutDown();
            
            this.removeRepository(dataset);
            
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        }
        return dataset;      
    }
    
    @Override
    public RMLDataset generateTriplesMapTriples(
            TriplesMap triplesMap, Map<String, String> parameters,
            String[] exeTriplesMap, RMLDataset originalDataset) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
        Repository repository ;
        log.debug("Generating Triples Map triples with metadata");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String startDateTime = sdf.format(new Date());
        
        if (dataset.getMetadataLevel().equals("triplesmap") ||
                dataset.getMetadataLevel().equals("triple")) {
            
            String[] name = triplesMap.getName().split("#");            
            generateRepository(name[1]);
            try {
                repository = manager.getRepository(name[1]);
                dataset.setRepository(repository);

                if (repository != null && !repository.isInitialized()) {
                    repository.initialize();
                }
            } catch (RepositoryConfigException ex) {
                log.error("Repository Config Exception " + ex);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }

            dataset.setNumbers();
            dataset = (MetadataRMLDataset) super.generateTriplesMapTriples(
                    triplesMap, parameters, exeTriplesMap, dataset);
                  
        } else {
            log.debug("Default repository");
            try {
                repository = manager.getRepository(dataset.getID());
                dataset.setRepository(repository);

                if (repository != null && !repository.isInitialized()) {
                    repository.initialize();
                }

            } catch (RepositoryConfigException ex) {
                log.error("Repository Config Exception " + ex);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            }
            dataset = (MetadataFileDataset) super.generateTriplesMapTriples(
                    triplesMap, parameters, exeTriplesMap, dataset);
        }
        
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String endDateTime = sdf.format(new Date());//.replace(' ', 'T') + 'Z';
        try {
            dataset.setRepository(manager.getRepository("metadata"));
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
        if (dataset.getMetadataLevel().equals("triplesmap") ||
                dataset.getMetadataLevel().equals("triple")) {
            metadataGenerator.generateTriplesMapMetaData(
                    dataset, triplesMap, pathToNativeStore,
                    startDateTime, endDateTime, manager);
        }

        return dataset;
    }
    
    @Override
    public RMLDataset chooseSesameDataSet(String repositoryID,
            String pathToNativeStore, String outputFormat){

            RMLDataset dataset;
            
            if (pathToNativeStore != null) {
                log.debug("Using direct file " + pathToNativeStore);
                dataset = new MetadataFileDataset(manager,
                        pathToNativeStore, outputFormat, repositoryID);
            } else {
                log.debug("Using default store (memory) ");
                dataset = new StdRMLDataset();
            }
                    
        return dataset;
    }
    
    @Override
    public RMLProcessor generateRMLProcessor(
            TriplesMap triplesMap, Map<String, String> parameters) {
        RMLProcessor processor = 
                super.generateRMLProcessor(triplesMap, parameters);
        processor.setMetadataGenerator(metadataGenerator);

        return processor;
    }
    
    
    public void writeRepositories(MetadataRMLDataset dataset,
            Collection<TriplesMap> triplesMaps, String pathToStore) {
        Iterator<TriplesMap> iterator = triplesMaps.iterator();
        FileOutputStream output = null;
        Repository repository ;

        while (iterator.hasNext()) {
            TriplesMap triplesMap = iterator.next();
            String[] name = triplesMap.getName().split("#");
            String path = pathToStore.replaceAll("\\.[a-zA-Z0-9]*", "_" + name[1] + ".ttl");
            File target = new File(path);
            //Prepare writer
            if (target.exists()) {
                try {
                    target.delete();
                    target.createNewFile();
                } catch (IOException ex) {
                    log.error("IO Exception " + ex);
                }
            }
            try {
                output = new FileOutputStream(target);
            } catch (FileNotFoundException ex) {
                log.error("File Not Found Exception " + ex);
            }

            try {
                dataset.setRepository(manager.getRepository(name[1]));
                repository = dataset.getRepository();
                RepositoryConnection con = repository.getConnection();
                RDFWriter writer = Rio.createWriter(this.format, output);

                con.export(writer);
                con.commit();
                con.close();
                
                manager.removeRepository(name[1]);
                repository.shutDown();
            } catch (RepositoryConfigException ex) {
                log.error("Repository Config Exception " + ex);
            } catch (RepositoryException ex) {
                log.error("Repository Exception " + ex);
            } catch (RDFHandlerException ex) {
                log.error("RDF Handler Exception " + ex);
            } finally {
                try {
                    output.close();
                } catch (IOException ex) {
                    log.error("IO Exception " + ex);
                }
            }

        }
    }

    
    public void removeRepository(
            RMLDataset originalDataset, String pathToStore) {
        FileOutputStream output = null;
        try {
            MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
            Repository repository = dataset.getRepository();
            RepositoryConnection con = repository.getConnection();
            File target = new File(pathToStore); 
            //Prepare writer
            if (target.exists()) {
                try {
                    target.delete();
                    target.createNewFile();
                } catch (IOException ex) {
                    log.error("IO Exception " + ex);
                }
            }
            output = new FileOutputStream(target);
            RDFWriter writer = Rio.createWriter(this.format, output);
            
            con.export(writer);
            con.commit();
            con.close();

        } catch (FileNotFoundException ex) {
            log.error("File Not Found Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } catch (RDFHandlerException ex) {
            log.error("RDF Handler Exception " + ex);
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                log.error("IO Exception " + ex);
            }
        }
    }
    
    public void removeRepository(RMLDataset dataset) {
        String file = manager.getBaseDir()  + "/repositories";
        
        dataset.closeRepository();
        manager.shutDown();
        
        try {
            FileUtils.deleteDirectory(new File(file));
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
        log.debug("Repository was successfully removed.");
    }
    
    
    public void generateRepositoryManager() {
        String indexes = "spoc";
        repositoryTypeSpec = 
                    new SailRepositoryConfig(new NativeStoreConfig(indexes));
    }
    
    public String generateRepositoryIDFromFile(String file){
        String repositoryID = 
                file.replaceAll("[a-zA-Z\\/]*/([a-zA-Z_.]*)", "$1").replaceAll(".ttl", "");
        return repositoryID;
    }
    
    public void generateRepository(String repositoryID) {
        RepositoryConfig repConfig ;
        repositoryTypeSpec =
                new SailRepositoryConfig(new NativeStoreConfig("spoc"));
        repConfig =
                new RepositoryConfig(repositoryID, repositoryTypeSpec);
        try {
            manager.addRepositoryConfig(repConfig);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        }

        Repository repository;
        try {
            repository = manager.getRepository(repositoryID);

            //Clean up repo from previous use
            if (!repository.isInitialized()) {
                repository.initialize();
            }
            RepositoryConnection con = repository.getConnection();
            con.clear();
            con.commit();
            con.close();
            repository.shutDown();
        } catch (RepositoryConfigException ex) {
            log.error("Repository Config Exception " + ex);
        } catch (RepositoryException ex) {
            log.error("Repository Exception " + ex);
        }
    }                    
}
