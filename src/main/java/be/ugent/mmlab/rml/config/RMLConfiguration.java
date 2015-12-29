package be.ugent.mmlab.rml.config;

import static be.ugent.mmlab.rml.config.RMLConfiguration.getCliOptions;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * Configuration file
 *
 * @author andimou
 */
public class RMLConfiguration {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(RMLConfiguration.class);
    private static final Options cliOptions = generateCLIOptions();
    
    public static CommandLine parseArguments(String[] args) throws ParseException {
        
        CommandLineParser cliParser = new GnuParser();
        return cliParser.parse(getCliOptions(), args);
    }

    private static Options generateCLIOptions() {
        Options cliOptions = new Options();
        
        cliOptions.addOption("h", "help", false, 
                "show this help message");
        cliOptions.addOption("m", "mapping document", true, 
                "the URI of the mapping file (required)");
        cliOptions.addOption("o", "output file", true, 
                "the URI of the output file (required)");
        cliOptions.addOption("f", "file format", true, 
                "the output format of the results: turtle, n3, ntriples (default), rdfxml (optional)");
        cliOptions.addOption("tm", "Triples Map", true, "Triples Map to be executed.");
        cliOptions.addOption("p", 
                "arguments to pass if the rml:source of the mapping document is a URI template "
                + "and requires parameters (they should be comma separated)", true, 
                "arguments for the source template");
        cliOptions.addOption("g", "graph", false, 
                "the graph to use");
        cliOptions.addOption("md", "md", true, 
                "the metadata vocabulary: prov, void, dcat");
        cliOptions.addOption("mdl", "mdl", true, 
                "the metadata level: dataset (default), triplesmap, triple");
        cliOptions.addOption("mdf", "mdf", true, 
                "the metadata format: same as output by default");
        cliOptions.addOption("s", "schemas", false, 
                "schemas");
        //cliOptions.addOption("t", "tests are enabled", false, 
        //      "the RDFUnit tests are called");
        return cliOptions;
    }
    
    public static Options getCliOptions() {
        return cliOptions;
    }
    
    public static void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("RML Processor", getCliOptions());
        System.exit(1);
    }
    
    public static String[] processTriplesMap (String parameters, String map_doc){
        if (parameters != null) {
            String[] exeTriplesMap = parameters.split(",");
            for(int i=0 ; i < exeTriplesMap.length ; i++){
                //TODO:remove hardcoded file:
                //TODO: Consider also 
                File file = new File(map_doc);
                exeTriplesMap[i] = "file:" + file.getAbsolutePath() + "#" + exeTriplesMap[i];
            log.info("TriplesMap to be processed " + exeTriplesMap[i]);
            }
            return exeTriplesMap;
        } else {
            return null;
        }
    }
    
}
