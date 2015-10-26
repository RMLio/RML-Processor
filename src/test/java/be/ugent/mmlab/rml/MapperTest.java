package be.ugent.mmlab.rml;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import junit.framework.TestSuite;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;

/**
 * Unit test for simple App.
 */
public class MapperTest
        extends TestCase {
    
    // Log
    private static final Logger log = 
            LogManager.getLogger(MapperTest.class);

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
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
   
   public void testExample1b() {
        URL fileToRMLFile = getClass().getResource("/example1/example1b.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example1/example.output.ttl");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("filename","src/test/resources/example1/example1.xml");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, parameters, null)));
        //assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }

    public void testExample2() {
        URL fileToRMLFile = getClass().getResource("/example2/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example2/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    /*public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }*/

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    /*public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }*/
    
    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
    
    /*public void testExample7() {
        URL fileToRMLFile = getClass().getResource("/example7/example7.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example7/example7.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }*/
    
    /*public void testExample8() {
        URL fileToRMLFile = getClass().getResource("/example8/simergy.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example8/simergy.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }*/
    
    public void testExample9() {
        URL fileToRMLFile = getClass().getResource("/example9/Vol-1128.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example9/Vol-1128.nt");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number","1128");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, parameters, null)));
    }
    
    public void testExampleXLSX() {
        URL fileToRMLFile = getClass().getResource("/exampleXLSX/Embrapa.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleXLSX/Embrapa.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
    
    public void testExampleCSVW() {
        URL fileToRMLFile = getClass().getResource("/exampleCSVW/exampleAirport.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleCSVW/exampleAirport.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
    
    public void testExampleDCAT() {
        URL fileToRMLFile = getClass().getResource("/exampleDCAT/exampleAirport.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleDCAT/exampleAirport.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
    
    public void testExampleHydra() {
        URL fileToRMLFile = getClass().getResource("/exampleHydra/exampleHydra.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleHydra/exampleHydra.output.ttl");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("publication_id","4384220");
        parameters.put("format","json");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, parameters, null)));
        //assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null)));
    }
    
    public void testExampleHydraA() {
        URL fileToRMLFile = getClass().getResource("/exampleHydra/exampleHydraA.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleHydra/exampleHydraA.output.ttl");
        String[] triplesMap = {"http://example.com/base#Paper"};
        
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }
    
    public void testExampleHydraB() {
        URL fileToRMLFile = getClass().getResource("/exampleHydra/exampleHydraB.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleHydra/exampleHydraB.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }
    
    /*public void testExampleHydraPagedCollection() {
        log.debug("Hydra Pagged Collection example has been started.");
        URL fileToRMLFile = getClass().getResource(
                "/exampleHydraPagedCollection/exampleHydraPubs.rml.ttl");
        log.debug("fileToRMLFile " + fileToRMLFile.toString());
        URL fileToOutputFile = getClass().getResource(
                "/exampleHydraPagedCollection/exampleHydra.output.ttl");
        log.debug("fileToOutputFile " + fileToOutputFile.toString());
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }*/
    
    public void testExampleHydraSPARQLsd() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleHydraSPARQLsd/exampleHydraSPARQLsd.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleHydraSPARQLsd/exampleHydraSPARQLsd.output.ttl");
        String[] triplesMap = {"http://example.com/base#Paper"};
        
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }
    
    private RMLDataset desiredOutput (URL outputURL){
        RMLDataset desiredOutput = new StdRMLDataset(false);
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.TURTLE);
        return desiredOutput;
    }
    
    /*private RMLDataset desiredContextOutput (URL outputURL){
        RMLDataset desiredOutput = new StdRMLDataset(false);
        desiredOutput.addFile(outputURL.getFile(), RDFFormat.NQUADS);
        return desiredOutput;
    }*/
    
    private RMLDataset assertMap(URL mappingURL, 
            Map<String, String> parameters, String[] triplesMap) {
        try {
            StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
            //Retrieve the Mapping Document
            log.info("========================================");
            log.info("Retrieving the RML Mapping Document..");
            log.info("========================================");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = mapDocRetrieval.getMappingDoc(
                    mappingURL.getFile(), RDFFormat.TURTLE);
            
            log.info("========================================");
            log.info("Extracting the RML Mapping Definitions..");
            log.info("========================================");
            RMLEngine engine = new RMLEngine();
            RMLMapping mapping = mappingFactory.extractRMLMapping(repository);
            
            log.info("========================================");
            log.info("Running the RML Mapping..");
            log.info("========================================");
            RMLDataset output = engine.runRMLMapping(
                    mapping, "http://example.com", parameters, triplesMap);
            if(output != null)
                output.dumpRDF(System.out, RDFFormat.TURTLE);

            return output;

        } catch (Exception ex) {
            log.error(ex);
        }

        return null;
    }
}
