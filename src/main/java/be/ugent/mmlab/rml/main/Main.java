package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.config.RMLConfiguration;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author mielvandersande, andimou
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Log
        org.apache.log4j.Logger log = LogManager.getLogger(Main.class);
        String map_doc = null, parameter = null, triplesMap = null;
        String[] exeTriplesMap = null;
        BasicConfigurator.configure();
        CommandLine commandLine;
        
        try {
            commandLine = RMLConfiguration.parseArguments(args);
            String outputFile = null, outputFormat = null;
            String graphName = "";

            if (commandLine.hasOption("h")) {
                RMLConfiguration.displayHelp();
            }
            if (commandLine.hasOption("f")) {
                outputFile = commandLine.getOptionValue("f", null);
            }
            if (commandLine.hasOption("o")) {
                outputFormat = commandLine.getOptionValue("o", null);
            }
            if (commandLine.hasOption("g")) {
                graphName = commandLine.getOptionValue("g", "");
            }
            if (commandLine.hasOption("m")) {
                map_doc = commandLine.getOptionValue("m", null);
            }
            if (commandLine.hasOption("p")) {
                parameter = commandLine.getOptionValue("p", null);
            }
            if (commandLine.hasOption("tm")) {
                triplesMap = commandLine.getOptionValue("tm", null);
                if(triplesMap != null)
                    exeTriplesMap = RMLConfiguration.processTriplesMap(triplesMap,map_doc);
            }
            
            RMLMappingFactory mappingFactory = new RMLMappingFactory();

            RMLMapping mapping = mappingFactory.extractRMLMapping(map_doc);

            RMLEngine engine = new RMLEngine();

            engine.runRMLMapping(mapping, graphName, outputFile, outputFormat, parameter, exeTriplesMap, true);        
            
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("RML Processor");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("");
            System.out.println("Usage: mvn exec:java -Dexec.args=\"<mapping_file> <output_file> [-g <graph>]\"");
            System.out.println("");
            System.out.println("With");
            System.out.println("    <mapping_file> = The RML mapping document conform with the RML specification (http://semweb.mmlab.be/rml/spec.html)");
            System.out.println("    <output_file> = The file where the output RDF triples are stored; default in Turtle (http://www.w3.org/TR/turtle/) syntax.");
            System.out.println("    <graph> (optional) = The named graph in which the output RDF triples are stored.");
            System.out.println("");
            System.out.println("--------------------------------------------------------------------------------");
            //}
        } catch (IOException | RepositoryException | RDFParseException | SQLException ex) {
            log.error(ex);
            RMLConfiguration.displayHelp();
        } catch (ParseException ex) {
            log.error(ex);
            RMLConfiguration.displayHelp();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
