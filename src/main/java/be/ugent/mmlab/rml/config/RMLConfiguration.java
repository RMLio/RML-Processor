/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.config;

import static be.ugent.mmlab.rml.config.RMLConfiguration.getCliOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author andimou
 */
public class RMLConfiguration {
    private static final Options cliOptions = generateCLIOptions();
    
    public static CommandLine parseArguments(String[] args) throws ParseException {
        
        CommandLineParser cliParser = new GnuParser();
        return cliParser.parse(getCliOptions(), args);
    }

    private static Options generateCLIOptions() {
        Options cliOptions = new Options();
        
        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("m", "mapping document", true, "the URI of the mapping file (required)");
        cliOptions.addOption("f", "output file", true, "the URI of the output file (required)");
        cliOptions.addOption("o", "file format", true, 
                "the output format of the results: turtle, n3, ntriples (default), rdfxml (optional)");
        cliOptions.addOption("p", "parameter", true, "");
        //cliOptions.addOption("p", 
        //        "arguments to pass if the rml:source of the mapping document is a URI template "
        //        + "and requires parameters (they should be comma separated)", false, 
        //        "arguments for the source template");
        cliOptions.addOption("g", "graph", false, "the graph to use");
        cliOptions.addOption("prov", "prov", false, "the provenance graph");
        cliOptions.addOption("s", "schemas", false, "schemas");
        //cliOptions.addOption("t", "tests are enabled", false, "the RDFUnit tests are called");
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
    
}
