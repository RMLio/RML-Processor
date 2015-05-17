package be.ugent.mmlab.rml;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
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
import org.apache.log4j.LogManager;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 * Unit test for simple App.
 */
public class MapperTest
        extends TestCase {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(MapperTest.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MapperTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MapperTest.class);
    }

    /**
     * Tests
     */
    public void testExample1() {
        URL fileToRMLFile = getClass().getResource("/example1/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example1/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }

    public void testExample2() {
        URL fileToRMLFile = getClass().getResource("/example2/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example2/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }

    public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    } 

    /*public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile)));
    }
    }*/
    
    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample7() {
        URL fileToRMLFile = getClass().getResource("/example7/example7.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example7/example7.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    } 
    
    public void testExample11() {
        URL fileToRMLFile = getClass().getResource("/example11/example11.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example11/example11.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample12() {
        URL fileToRMLFile = getClass().getResource("/example12/example12.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example12/example12.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample14() {
        URL fileToRMLFile = getClass().getResource("/example14/example14.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example14/example14.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample14b() {
        URL fileToRMLFile = getClass().getResource("/example14/example14b.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example14/example14b.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample14c() {
        URL fileToRMLFile = getClass().getResource("/example14/example14c.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example14/example14c.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample15() {
        URL fileToRMLFile = getClass().getResource("/example15/example15.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example15/example15.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample16() {
        URL fileToRMLFile = getClass().getResource("/example16/example16.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example16/example16.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample16a() {
        URL fileToRMLFile = getClass().getResource("/example16/example16a.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example16/example16a.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample16b() {
        URL fileToRMLFile = getClass().getResource("/example16/example16b.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example16/example16b.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample17() {
        URL fileToRMLFile = getClass().getResource("/example17/example17.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example17/example17.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample18() {
        URL fileToRMLFile = getClass().getResource("/example18/example18.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example18/example18.output.ttl");
        String[] triplesMap = {"http://example.com/base#Paper"} ;
        //assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, triplesMap)));
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample19() {
        URL fileToRMLFile = getClass().getResource("/example19/example19.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example19/example19.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample19b() {
        URL fileToRMLFile = getClass().getResource("/example19/example19b.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example19/example19b.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    private RMLSesameDataSet desiredOutput (URL outputURL){
        RMLSesameDataSet desiredOutput = new RMLSesameDataSet();
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);
        return desiredOutput;
    }
    
    private RMLSesameDataSet desiredContextOutput (URL outputURL){
        RMLSesameDataSet desiredOutput = new RMLSesameDataSet();
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.NQUADS);
        return desiredOutput;
    }
    
    private SesameDataSet assertMap(URL mappingURL, String[] triplesMap) {
        try {
            RMLMappingFactory mappingFactory = new RMLMappingFactory();
            RMLMapping mapping = mappingFactory.extractRMLMapping(mappingURL.getFile());

            RMLEngine engine = new RMLEngine();
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com", triplesMap);

            output.dumpRDF(System.out, RDFFormat.TURTLE);

            return output;

        } catch (SQLException | InvalidR2RMLStructureException | InvalidR2RMLSyntaxException |
                R2RMLDataError | RepositoryException | RDFParseException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MapperTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
