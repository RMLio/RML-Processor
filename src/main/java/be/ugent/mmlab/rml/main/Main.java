package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.config.RMLConfiguration;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.StdRMLEngine;
import be.ugent.mmlab.rml.core.StdMetadataRMLEngine;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.BasicConfigurator;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class Main {
    // Log
    static Logger log = LoggerFactory.getLogger(
            Main.class.getPackage().toString());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String map_doc = null, triplesMap ;
        String[] exeTriplesMap = null;
        Map<String, String> parameters = null;
        BasicConfigurator.configure();
        CommandLine commandLine;
        StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
        
        log.info("RML Processor");
        log.info("=================================================");
        log.info("");

        try {
            commandLine = RMLConfiguration.parseArguments(args);
            String outputFile = null, outputFormat = "turtle";
            String graphName = "", metadataVocab = null, metadataLevel = "None", 
                    metadataFormat = null;

            if (commandLine.hasOption("h")) {
                RMLConfiguration.displayHelp();
            }
            if (commandLine.hasOption("o")) {
                outputFile = commandLine.getOptionValue("o", null);
            }
            if (commandLine.hasOption("g")) {
                graphName = commandLine.getOptionValue("g", "");
            }
            if (commandLine.hasOption("p")) {
                parameters = retrieveParameters(commandLine);
            }
            if (commandLine.hasOption("f")) {
                outputFormat = commandLine.getOptionValue("f", null);
            }
            
            if (commandLine.hasOption("m")) {
                map_doc = commandLine.getOptionValue("m", null);
            }
            
            if (commandLine.hasOption("md")) {
                metadataVocab = commandLine.getOptionValue("md", null);
            }
            
            if (commandLine.hasOption("mdl")) {
                metadataLevel = commandLine.getOptionValue("mdl", null);
            }
            
            if (commandLine.hasOption("mdf")) {
                metadataFormat = commandLine.getOptionValue("mdf", null);
            }
            
            //Retrieve the Mapping Document
            log.info("========================================");
            log.info("Retrieving the RML Mapping Document...");
            log.info("========================================");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = 
                    mapDocRetrieval.getMappingDoc(map_doc, RDFFormat.TURTLE);
            
            if(repository == null){
                log.debug("Problem retrieving the RML Mapping Document");
                System.exit(1);
            }
            
            log.info("========================================");
            log.info("Extracting the RML Mapping Definitions..");
            log.info("========================================");
            RMLMapping mapping = mappingFactory.extractRMLMapping(repository);
            
            log.debug("Generation Execution plan...");
            if (commandLine.hasOption("tm")) {
                triplesMap = commandLine.getOptionValue("tm", null);
                if(triplesMap != null)
                    exeTriplesMap = 
                            RMLConfiguration.processTriplesMap(triplesMap,map_doc);
            }
            
            log.info("========================================");
            log.info("Running the RML Mapping..");
            log.info("========================================");
            
            if(metadataLevel.equals("None") && metadataFormat == null){
                runWithoutMetadata(mapping, outputFile, outputFormat, 
                        graphName, parameters, exeTriplesMap); 
            }
            else {
                runWithMetadata(mapping, outputFile, outputFormat, 
                        graphName, parameters, exeTriplesMap, 
                        metadataLevel, metadataFormat, metadataVocab);
            }

            System.exit(0);
            
        } catch (Exception ex) {
            System.exit(1);
            log.error("Exception " + ex);
            RMLConfiguration.displayHelp();
        } 

    }
    
    private static void runWithoutMetadata( 
            RMLMapping mapping, String outputFile, String outputFormat, 
            String graphName, Map<String,String> parameters, String[] exeTriplesMap) {
        RMLEngine engine; 
        RMLDataset dataset;
        log.debug("Running without metadata...");

        //RML Engine that does not generate metadata
        engine = new StdRMLEngine(outputFile);
        dataset = engine.chooseSesameDataSet(
                "dataset", outputFile, outputFormat);

        engine.runRMLMapping(
                dataset, mapping, graphName, parameters, exeTriplesMap);
        
        dataset.closeRepository();
    }
    
    private static void runWithMetadata(
            RMLMapping mapping, String outputFile, String outputFormat,
            String graphName, Map<String, String> parameters, String[] exeTriplesMap, 
            String metadataLevel, String metadataFormat, String metadataVocab) {
        StdMetadataRMLEngine engine; 
        MetadataRMLDataset dataset;
        
        //If not user-defined, use same as for the output
        if (metadataFormat == null) {
            metadataFormat = outputFormat;
        }
        
        //RML Engine that generates metadata too
        engine = new StdMetadataRMLEngine(outputFile);

        //generate the repository ID for the metadata graph
        String metadataRepositoryID = "metadata";
        //Generate the repository for the metadata graph
        engine.generateRepository(metadataRepositoryID);
        //generate the repository ID for the metadata graph
        String datasetRepositoryID = 
                engine.generateRepositoryIDFromFile(outputFile);
        //Generate the repository for the actual dataset
        engine.generateRepository(datasetRepositoryID);

        //Generate dataset for the actual dataset graph
        dataset = (MetadataRMLDataset) engine.chooseSesameDataSet(
                    datasetRepositoryID, outputFile, outputFormat);
        //Set dataset metadata
        dataset.setDatasetMetadata(metadataLevel, metadataFormat, metadataVocab);

        engine.runRMLMapping(
                dataset, mapping, graphName, parameters, exeTriplesMap);
        
        File file = new File(dataset.getRepository().getDataDir().getParent());
        boolean out = file.delete(); 
        
    }
       
    /**
     *
     * @param commandLine
     * @return
     */
    public static Map<String, String> retrieveParameters(CommandLine commandLine) {
        Map<String, String> parameters = new HashMap<String, String>();
        String[] parameterKeyValue;

        String parameter = commandLine.getOptionValue("p", null);
        String[] subParameters = parameter.split(",");
        for (String subParameter : subParameters) {
            parameterKeyValue = subParameter.split("=");

            String key = parameterKeyValue[0];
            String value = parameterKeyValue[1];

            parameters.put(key, value);
        }

        return parameters;
    }
}
