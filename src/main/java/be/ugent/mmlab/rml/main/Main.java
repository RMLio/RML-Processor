package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author mielvandersande
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String graphName = "";

            switch (args.length) {
                case 4:
                    graphName = args[3];
                case 3:
                    FileInputStream input = new FileInputStream(args[0]);
                    //load the properties
                    RMLEngine.getFileMap().load(input);

                    RMLMapping mapping = RMLMappingFactory.extractRMLMapping(args[1]);
                    RMLEngine engine = new RMLEngine();

                    engine.runRMLMapping(mapping, graphName, args[2], true);
                    //SesameDataSet output = engine.runRMLMapping(mapping, graphName);

                    //output.dumpRDF(args[2], RDFFormat.TURTLE);

                    break;
                default:
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("RML Processor");
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("");
                    System.out.println("Usage: mvn exec:java -Dexec.args=\"<sources_properties> <mapping_file> <output_file> [<graph>]\"");
                    System.out.println("");
                    System.out.println("With");
                    System.out.println("    <sources_properties> = Java properties file containing key-value pairs which configure the data sources used in the mapping file.");
                    System.out.println("    <mapping_file> = The RML mapping file conform with the RML specification (http://semweb.mmlab.be/ns/rml)");
                    System.out.println("    <output_file> = The file where the output RDF triples are stored; default in Turtle (http://www.w3.org/TR/turtle/) syntax.");
                    System.out.println("    <graph> (optional) = The named graph in which the output RDF triples are stored.");
                    System.out.println("");
                    System.out.println("    An example '<sources_properties>' file 'sources.properties' could contain:");
                    System.out.println("");
                    System.out.println("    #File: sources.properties");
                    System.out.println("    file1=/path/to/file1.csv");
                    System.out.println("    file2=/path/to/file2.json");
                    System.out.println("    file3=/path/to/file3.xml");
                    System.out.println("--------------------------------------------------------------------------------");
            }
        } catch (IOException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException | R2RMLDataError | RepositoryException | RDFParseException | SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
