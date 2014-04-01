package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author mielvandersande, andimou
 */
public class Main {
   
    private static Log log = LogFactory.getLog(Main.class);


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String graphName = "", keyword = null;
            // create Options object
            Options options = new Options();            
            // add options
            options.addOption("out", true, "outuput file");
            options.addOption("sp", true, "source properties file");
            options.addOption("g", true, "Graph name");
            options.addOption("k", true, "keyword");
            //should be new DefaultParser() but requires cli 1.3 instead of clli 1.2
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse( options, args);
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(args[0]);
            RMLEngine engine = new RMLEngine();
            log.info("mapping document " + args[0]);
            
            if(cmd.hasOption("k"))
                keyword = cmd.getOptionValue("k");
            
            FileInputStream source_properties = null;    
            if(cmd.hasOption("sp")) {
                source_properties = new FileInputStream(cmd.getOptionValue("sp"));
                log.info("source properties parameter is equal to " + cmd.getOptionValue("sp"));
                //load the properties
                RMLEngine.getFileMap().load(source_properties);
                //engine.runRMLMapping(mapping, graphName, args[1], true, true, keyword);
                if(cmd.hasOption("out"))
                    engine.runRMLMapping(mapping, graphName, cmd.getOptionValue("out"), true, true, keyword);
                else
                    engine.runRMLMapping(mapping, graphName, null, true, true, keyword);
            }
            else
                //engine.runRMLMapping(mapping, graphName, args[1], true, false, keyword);
                if(cmd.hasOption("out"))
                    engine.runRMLMapping(mapping, graphName, cmd.getOptionValue("out"), true, false, keyword);
                else
                    engine.runRMLMapping(mapping, graphName, null, false, false, keyword);
            if(cmd.hasOption("g")) 
                graphName = cmd.getOptionValue("g");           

        } catch (IOException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException | R2RMLDataError | RepositoryException | RDFParseException | SQLException ex) {
            log.error(ex.getMessage());
        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
