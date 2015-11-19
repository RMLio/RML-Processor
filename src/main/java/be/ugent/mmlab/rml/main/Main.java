package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.config.RMLConfiguration;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.StdRMLEngine;
import be.ugent.mmlab.rml.core.RMLEngineMeta;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
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
    static Logger log = LoggerFactory.getLogger(Main.class);

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
        
        System.out.println("-------------------------------------------------");
        System.out.println("RML Processor");
        System.out.println("-------------------------------------------------");
        System.out.println("");

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
            
            if (commandLine.hasOption("tm")) {
                triplesMap = commandLine.getOptionValue("tm", null);
                if(triplesMap != null)
                    exeTriplesMap = 
                            RMLConfiguration.processTriplesMap(triplesMap,map_doc);
            }
            
            log.info("========================================");
            log.info("Running the RML Mapping..");
            log.info("========================================");
            RMLDataset dataset;

            if(metadataLevel.equals("None") && metadataFormat == null){
                log.debug("Running without...");
                dataset = runWithoutMetadata(mapping, outputFile, outputFormat, 
                        graphName, parameters, exeTriplesMap); 
            }
            else {
                log.debug("metadataFormat " + metadataFormat);
                dataset = runWithMetadata(mapping, outputFile, outputFormat, 
                        graphName, parameters, exeTriplesMap, 
                        metadataLevel, metadataFormat, metadataVocab);
            }
            //TODO: Do I actual need it? I think I close earlier
            dataset.closeRepository();
            System.exit(0);
            
        } catch (Exception ex) {
            System.exit(1);
            log.error("Exception " + ex);
            RMLConfiguration.displayHelp();
        } 

    }
    
    private static RMLDataset runWithoutMetadata( 
            RMLMapping mapping, String outputFile, String outputFormat, 
            String graphName, Map<String,String> parameters, String[] exeTriplesMap) {
        RMLEngine engine; 
        RMLDataset dataset;
        log.debug("Running without metadata...");

        //RML Engine that does not generate metadata
        engine = new StdRMLEngine(outputFile);
        log.debug("RML Engine is generated.");
        dataset = engine.chooseSesameDataSet(
                "dataset", outputFile, outputFormat);
        log.debug("Dataset is generated.");
        //TODO: Do I need the returned dataset?
        dataset = engine.runRMLMapping(
                dataset, mapping, graphName, parameters, exeTriplesMap);
        return dataset;
    }
    
    private static RMLDataset runWithMetadata(
            RMLMapping mapping, String outputFile, String outputFormat,
            String graphName, Map<String, String> parameters, String[] exeTriplesMap, 
            String metadataLevel, String metadataFormat, String metadataVocab) {
        RMLEngine engine; 
        RMLDataset dataset, metadataDataset;
        
        log.debug("Running with metadata...");
        
        //generate the file that contains the metadata graph
        String pathToMetadataStore =
                outputFile.replaceAll("(\\.[a-zA-Z0-9]*$)", ".metadata$1");
        
        engine = new RMLEngineMeta(outputFile);
        
        //RML Rngine that generates metadata too
        if (metadataFormat == null) {
            metadataDataset = engine.chooseSesameDataSet(
                    "metadata", pathToMetadataStore, outputFormat);
        } else {
            //generate dataset for the metadata graph
            metadataDataset = engine.chooseSesameDataSet(
                    "metadata", pathToMetadataStore, metadataFormat);
        }
        dataset = engine.chooseSesameDataSet(
                    "dataset", outputFile, outputFormat);

        dataset.setDatasetMetadata(
                metadataDataset, metadataLevel, metadataFormat, metadataVocab);
        
        dataset = engine.runRMLMapping(dataset, mapping, graphName,
                parameters, exeTriplesMap);
        return dataset;
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
