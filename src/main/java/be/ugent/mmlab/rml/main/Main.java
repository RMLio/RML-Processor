package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.config.RMLConfiguration;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.mapdochandler.extraction.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
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
        String map_doc = null, parameter = null, triplesMap ;
        String[] exeTriplesMap = null;
        BasicConfigurator.configure();
        CommandLine commandLine;
        StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
        RMLEngine engine = new RMLEngine();
        
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("RML Processor");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("");

        try {
            commandLine = RMLConfiguration.parseArguments(args);
            String outputFile = null, outputFormat = null;
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
                parameter = commandLine.getOptionValue("p", null);
            }
            if (commandLine.hasOption("f")) {
                outputFormat = commandLine.getOptionValue("f", null);
            }
            
            if (commandLine.hasOption("m")) {
                map_doc = commandLine.getOptionValue("m", null);
            }
            log.error("RML Processor - Extracting Mapping Document.");
            System.out.println("RML Processor - Extracting Mapping Document.");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            
            //Retrieve the Mapping Document
            log.info("Retrieving the Mapping Document..");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = mapDocRetrieval.getMappingDoc(map_doc, RDFFormat.TURTLE);
            
            RMLMapping mapping = mappingFactory.extractRMLMapping(repository);
            
            if (commandLine.hasOption("tm")) {
                triplesMap = commandLine.getOptionValue("tm", null);
                if(triplesMap != null)
                    exeTriplesMap = RMLConfiguration.processTriplesMap(triplesMap,map_doc);
            }
            log.error("RML Processor - Executing Mapping Document.");
            System.out.println("RML Processor - Executing Mapping Document.");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            
            engine.runRMLMapping(mapping, graphName, outputFile, outputFormat, parameter, exeTriplesMap);  
            
            System.exit(0);
            
            /*System.out.println("--------------------------------------------------------------------------------");
            System.out.println("RML Processor");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            System.out.println("Usage: java -jar -m <mapping_file> -o <output_file> -f format [-g <graph> -tm <TriplesMap>]");
            System.out.println("");
            System.out.println("With");
            System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
            System.out.println("    <output_file> = The output format (turtle, ntriples, n3, nquads, rdfxml, rdfjson, jsonld).");
            System.out.println("    <output_file> = The file where the output RDF triples are stored; default in Turtle (http://www.w3.org/TR/turtle/) syntax.");
            System.out.println("    <graph> (optional) = The named graph in which the output RDF triples are stored.");
            System.out.println("");
            System.out.println("--------------------------------------------------------------------------------");*/
            //}
        } catch (Exception ex) {
            System.exit(1);
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ex);
            RMLConfiguration.displayHelp();
        } 

    }
}
