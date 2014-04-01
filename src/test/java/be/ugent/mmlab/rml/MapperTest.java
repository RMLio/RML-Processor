package be.ugent.mmlab.rml;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import junit.framework.TestSuite;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 * Unit test for simple App.
 */
public class MapperTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MapperTest(String testName) {
        super(testName);

        //RMLEngine.getFileMap().put("example.xml", getClass().getResource("/example1/example.xml").getFile());
        //RMLEngine.getFileMap().put("Airport.csv", getClass().getResource("/example3/Airport.csv").getFile());
        //RMLEngine.getFileMap().put("Venue.json", getClass().getResource("/example3/Venue.json").getFile());
        //RMLEngine.getFileMap().put("Transport.xml", getClass().getResource("/example3/Transport.xml").getFile());
        //RMLEngine.getFileMap().put("Venue4.json", getClass().getResource("/example4/Venue.json").getFile());
        //RMLEngine.getFileMap().put("moon-walkers.csv", getClass().getResource("/example5/moon-walkers.csv").getFile());
        //RMLEngine.getFileMap().put("museum.json", getClass().getResource("/example5/museum.json").getFile());
        //RMLEngine.getFileMap().put("/example6/example.xml", getClass().getResource("/example6/example.xml").getFile());
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MapperTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testExample1() {
        URL fileToRMLFile = getClass().getResource("/example1/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example1/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample2() {
        URL fileToRMLFile = getClass().getResource("/example2/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example2/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    /*public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    
    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }
    
    public void testExample13() {
        URL fileToRMLFile = getClass().getResource("/example13/gemeentes.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example13/gemeentes.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }
    
    /*public void testExample7() {
        URL fileToRMLFile = getClass().getResource("/example7/taxpub_dtd.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example7/example.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    
    /*public void testExample12() {
        URL fileToRMLFile = getClass().getResource("/example12/jats_Adv_Virol.mapping.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example12/Adv_Virol_2010_Jan_18_2010_407476.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }
    
    public void testExample13() {
        URL fileToRMLFile = getClass().getResource("/example13/BMC_Pediatr_2013_Jan_15_13_8.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example13/BMC_Pediatr_2013_Jan_15_13_8.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    
    /*public void testExample8() {
        URL fileToRMLFile = getClass().getResource("/example8/simergy.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example8/simergy.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    
    private boolean assertMap(URL mappingURL, URL outputURL) {
        try {
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(mappingURL.getFile());

            RMLEngine engine = new RMLEngine();
            
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com");

            output.dumpRDF(System.out, RDFFormat.TURTLE);

            SesameDataSet desiredOutput = new SesameDataSet();
            desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);

            return desiredOutput.isEqualTo(output);
        } catch (SQLException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException | R2RMLDataError | RepositoryException | RDFParseException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
}
