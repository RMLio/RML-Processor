package be.ugent.mmlab.rml;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import junit.framework.TestSuite;

import org.apache.log4j.LogManager;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

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
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
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
    
    /*public void testExample8() {
        URL fileToRMLFile = getClass().getResource("/example8/simergy.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example8/simergy.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample8() {
        URL fileToRMLFile = getClass().getResource("/example8/symergy.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example8/symergy.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }*/
    
    public void testExample10() {
        URL fileToRMLFile = getClass().getResource("/example10/exampleAirport.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example10/exampleAirport.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    /*public void testExample11() {
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
    }*/
    
    /*public void testExample18() {
        URL fileToRMLFile = getClass().getResource("/example18/example18.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example18/example18.output.ttl");
        String[] triplesMap = {"http://example.com/base#Paper"} ;
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, triplesMap)));
        //assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }*/
    
    /*public void testExample19() {
        URL fileToRMLFile = getClass().getResource("/example19/example19.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example19/example19.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExample19b() {
        URL fileToRMLFile = getClass().getResource("/example19/example19b.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example19/example19b.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }*/
    
    private RMLSesameDataSet desiredOutput (URL outputURL){
        RMLSesameDataSet desiredOutput = new RMLSesameDataSet(false);
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);
        return desiredOutput;
    }
    
    private RMLSesameDataSet desiredContextOutput (URL outputURL){
        RMLSesameDataSet desiredOutput = new RMLSesameDataSet(false);
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.NQUADS);
        return desiredOutput;
    }
    
    private RMLSesameDataSet assertMap(URL mappingURL, String[] triplesMap) {
        try {
            StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
            //Retrieve the Mapping Document
            log.info("Retrieving the Mapping Document..");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = mapDocRetrieval.getMappingDoc(mappingURL.getFile(), RDFFormat.TURTLE);

            RMLEngine engine = new RMLEngine();
            RMLMapping mapping = mappingFactory.extractRMLMapping(repository);
            RMLSesameDataSet output = engine.runRMLMapping(mapping, "http://example.com", triplesMap);
            
            output.dumpRDF(System.out, RDFFormat.TURTLE);

            return output;

        } catch (Exception ex) {
            log.error(ex);
        }

        return null;
    }
}
