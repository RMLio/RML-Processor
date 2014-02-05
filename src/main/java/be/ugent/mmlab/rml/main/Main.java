package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.FileInputStream;
import java.util.Properties;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.rio.RDFFormat;

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
                    RMLEngine.fileMap.load(input);
                    
                    RMLMapping mapping = RMLMappingFactory.extractRMLMapping(args[1]);
                    RMLEngine engine = new RMLEngine();
                    
                    SesameDataSet output = engine.runRMLMapping(mapping, graphName);

                    output.dumpRDF(args[2], RDFFormat.TURTLE);

                    break;
                default:
                    System.out.println("Usage: java -jar RMLMapper.jar <sources_properties> <mapping_file> <output_file> [<graph>]");
                    return;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }



    }
}
