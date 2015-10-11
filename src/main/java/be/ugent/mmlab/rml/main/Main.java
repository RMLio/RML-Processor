package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.config.RMLConfiguration;
import be.ugent.mmlab.rml.core.RMLEngine;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Log
        Logger log = LoggerFactory.getLogger(Main.class);
        String map_doc = null, triplesMap ;
        String[] exeTriplesMap = null;
        Map<String, String> parameters = null;
        BasicConfigurator.configure();
        CommandLine commandLine;
        StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
        RMLEngine engine = new RMLEngine();
        
        System.out.println("-------------------------------------------------");
        System.out.println("RML Processor");
        System.out.println("-------------------------------------------------");
        System.out.println("");

        try {
            commandLine = RMLConfiguration.parseArguments(args);
            String outputFile = null, outputFormat = "turtle";
            String graphName = "";

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
            
            //Retrieve the Mapping Document
            log.info("========================================");
            log.info("Retrieving the RML Mapping Document...");
            log.info("========================================");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = 
                    mapDocRetrieval.getMappingDoc(map_doc, RDFFormat.TURTLE);
            
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
            RMLDataset dataset = 
                    engine.runRMLMapping(mapping, graphName, outputFile, 
                    outputFormat, parameters, exeTriplesMap);  
            dataset.closeRepository();
            System.exit(0);
            
        } catch (Exception ex) {
            System.exit(1);
            log.error("Exception " + ex);
            RMLConfiguration.displayHelp();
        } 

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
