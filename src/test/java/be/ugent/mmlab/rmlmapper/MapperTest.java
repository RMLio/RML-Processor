package be.ugent.mmlab.rmlmapper;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertTrue;
import junit.framework.Test;
import junit.framework.TestCase;
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
        
        RMLEngine.fileMap = new HashMap<String, String>();
        RMLEngine.fileMap.put("example.xml", getClass().getResource("/example1/example.xml").getFile());
        //RMLEngine.fileMap.put("Airport.csv", getClass().getResource("/example3/Airport.csv").getFile());
        //RMLEngine.fileMap.put("Venue.json", getClass().getResource("/example3/Venue.json").getFile());
        RMLEngine.fileMap.put("Venue.json", getClass().getResource("/example4/Venue.json").getFile());
        //RMLEngine.fileMap.put("Transport.xml", getClass().getResource("/example3/Transport.xml").getFile());
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
    
    /*public void testExample3() {
            URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
            URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
            assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }*/
    
    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
            URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
            assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
        
    }
    
    private boolean assertMap(URL mappingURL, URL outputURL) {
        try {
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(mappingURL.getFile());

            RMLEngine engine = new RMLEngine();
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com");

            output.dumpRDF(System.out, RDFFormat.TURTLE);
            
            SesameDataSet desiredOutput = new SesameDataSet();
            desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);
            
            
            return desiredOutput.isEqualTo(output);
        } catch (SQLException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidR2RMLStructureException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidR2RMLSyntaxException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (R2RMLDataError ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
}
