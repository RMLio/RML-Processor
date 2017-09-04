package be.ugent.mmlab.rml;

import be.ugent.mmlab.logicaltarget.BusEventTypes;
import be.ugent.mmlab.logicaltarget.LogicalTargetBus;
import be.ugent.mmlab.logicaltarget.buffer.IterationBuffer;
import be.ugent.mmlab.logicaltarget.buffer.ProcessBuffer;
import be.ugent.mmlab.logicaltarget.buffer.TripleBuffer;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.StdRMLEngine;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.dataset.StdRMLDataset;
import be.ugent.mmlab.rml.mapdochandler.extraction.std.StdRMLMappingFactory;
import be.ugent.mmlab.rml.mapdochandler.retrieval.RMLDocRetrieval;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;

import static org.eclipse.rdf4j.rio.RDFFormat.*;

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

    public void testExample3() {
        URL fileToRMLFile = getClass().getResource("/example3/example3.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example3/example3.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExample4() {
        URL fileToRMLFile = getClass().getResource("/example4/example4_Venue.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example4/example4_Venue.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExample6() {
        URL fileToRMLFile = getClass().getResource("/example6/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example6/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleGraphMapSubMap() {
        URL fileToRMLFile = getClass().getResource("/exampleGraphMap/exampleGraphSubMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleGraphMap/exampleGraphSubMap.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleGraphMapPreMap() {
        URL fileToRMLFile = getClass().getResource("/exampleGraphMap/exampleGraphPreMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleGraphMap/exampleGraphPreMap.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleGraphMapObjMap() {
        URL fileToRMLFile = getClass().getResource("/exampleGraphMap/exampleGraphObjMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleGraphMap/exampleGraphObjMap.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleGraphMapRefObjMap() {
        URL fileToRMLFile = getClass().getResource("/exampleGraphMap/exampleGraphRefObjMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleGraphMap/exampleGraphRefObjMap.output.ttl");
        assertTrue(desiredContextOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExample8() {
        URL fileToRMLFile = getClass().getResource("/example8/simergy.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example8/simergy.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

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

//    public void testExampleFNO() {
//        URL fileToRMLFile = getClass().getResource("/exampleFNO/example_Venue.rml.ttl");
//        URL fileToOutputFile = getClass().getResource("/exampleFNO/example_Venue.output.ttl");
//        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
//                assertMap(fileToRMLFile, null, null)));
//    }
//
//    public void testExampleFN() {
//        URL fileToRMLFile = getClass().getResource("/exampleFN/mapping_isSet.rml.ttl");
//        URL fileToOutputFile = getClass().getResource("/exampleFN/example.output.ttl");
//        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
//                assertMap(fileToRMLFile, null, null)));
//    }

    public void testExampleFallback() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFallback/exampleFallback.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFallback/exampleFallback.output.ttl");
        String[] triplesMap = {"http://example.com/base#Conference"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleFallback_ObjMap() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFallback/exampleFallback_ObjMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFallback/exampleFallback_ObjMap.output.ttl");
        String[] triplesMap = {"http://example.com/base#Companies"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleEqualFallback_ObjMap() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFallback/exampleEqualFallback_ObjMap.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFallback/exampleEqualFallback_ObjMap.output.ttl");
        String[] triplesMap = {"http://example.com/base#Companies"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleEqualFallback_POM() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFallback/exampleEqualFallback_POM.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFallback/exampleEqualFallback_ObjMap.output.ttl");
        String[] triplesMap = {"http://example.com/base#Companies"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleEqual() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/example.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/example.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleSubject() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/exampleSubject.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/exampleSubject.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleSubject_b() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/exampleSubject_b.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/exampleSubject_b.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExamplePOM() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/examplePOM.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/examplePredicate.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExamplePredicate() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/examplePredicate.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/examplePredicate.output.ttl");
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleObject() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleEqual/exampleObject.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleEqual/exampleObject.output.ttl");
        RMLDataset smth = desiredOutput(fileToOutputFile);
        log.debug("smth " + smth);
        assertTrue(smth.isEqualTo(
                assertMap(fileToRMLFile, null, null)));
    }

    public void testExampleHydraSPARQLsd() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleHydraSPARQLsd/exampleHydraSPARQLsd.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleHydraSPARQLsd/exampleHydraSPARQLsd.output.ttl");
        String[] triplesMap = {"http://example.com/base#Paper"};

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleFnIsSet() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFn/mapping_isSet.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFn/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

//    public void testExampleFnSimilarity() {
//        URL fileToRMLFile = getClass().getResource(
//                "/exampleFn/mapping_similarity.rml.ttl");
//        URL fileToOutputFile = getClass().getResource(
//                "/exampleFn/example.output.ttl");
//        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};
//        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
//                assertMap(fileToRMLFile, null, triplesMap)));
//    }

    public void testExampleFnUppercase() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleFn/mapping_uppercase.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleFn/example_uppercase.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }

//    public void testExampleFnSubject() {
//        URL fileToRMLFile = getClass().getResource(
//                "/exampleFn/mapping_subject.rml.ttl");
//        URL fileToOutputFile = getClass().getResource(
//                "/exampleFn/example_subject.output.ttl");
//        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};
//        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
//                assertMap(fileToRMLFile, null, triplesMap)));
//    }

    /*public void testExampleDBP() {
        URL fileToRMLFile = getClass().getResource(
                "/exampleDBP/mapping.rml.ttl");
        URL fileToOutputFile = getClass().getResource(
                "/exampleDBP/example.output.ttl");
        String[] triplesMap = {"http://mappings.dbpedia.org/wiki/Mapping_en/Infobox_artist"};
        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(
                assertMap(fileToRMLFile, null, triplesMap)));
    }*/

    private RMLDataset desiredOutput (URL outputURL){
        RMLDataset desiredOutput = new StdRMLDataset(false);
        desiredOutput.addFile(outputURL.getFile(), TURTLE);
        return desiredOutput;
    }

    private RMLDataset desiredContextOutput (URL outputURL){
        RMLDataset desiredOutput = new StdRMLDataset(false);
        desiredOutput.addFile(outputURL.getFile(), NQUADS);
        return desiredOutput;
    }

    private RMLDataset assertMap(URL mappingURL,
            Map<String, String> parameters, String[] triplesMap) {
        RMLDataset dataset;
        try {
            StdRMLMappingFactory mappingFactory = new StdRMLMappingFactory();
            //Retrieve the Mapping Document
            log.info("========================================");
            log.info("Retrieving the RML Mapping Document..");
            log.info("========================================");
            RMLDocRetrieval mapDocRetrieval = new RMLDocRetrieval();
            Repository repository = mapDocRetrieval.getMappingDoc(
                    mappingURL.getFile(), TURTLE);

            log.info("========================================");
            log.info("Extracting the RML Mapping Definitions..");
            log.info("========================================");
            RMLEngine engine = new StdRMLEngine();
            RMLMapping mapping = mappingFactory.extractRMLMapping(repository);

            log.info("========================================");
            log.info("Running the RML Mapping..");
            log.info("========================================");
            RMLDataset output = engine.chooseSesameDataSet("dataset", null, null);
//            engine.generateRDFTriples
            output = engine.runRMLMapping(output,
                    mapping, "http://example.com", parameters, triplesMap);
            if(output != null)
                output.dumpRDF(System.out, TURTLE);

            return output;

        } catch (Exception ex) {
            log.error(ex);
        }

        return null;
    }

    /*
      Logical target tests
     */
    public void testExampleLogicalTargetIteration() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_iteration.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstIteration = new ArrayList<Statement>();
        ArrayList secondIteration = new ArrayList<Statement>();
        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));


        firstIteration.add(triple1);
        firstIteration.add(triple2);

        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));

        secondIteration.add(triple3);
        secondIteration.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, firstIteration, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, secondIteration, 1));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetTriple() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_triple.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstTriple = new ArrayList<Statement>();
        ArrayList secondTriple = new ArrayList<Statement>();
        ArrayList thirdTriple = new ArrayList<Statement>();
        ArrayList fourthTriple = new ArrayList<Statement>();

        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        firstTriple.add(triple1);
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));
        secondTriple.add(triple2);
        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        thirdTriple.add(triple3);
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));
        fourthTriple.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, firstTriple, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, secondTriple, 1));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, thirdTriple, 2));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, fourthTriple, 3));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetProcess() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_process.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList endProcess = new ArrayList<Statement>();

        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        endProcess.add(triple1);
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));
        endProcess.add(triple2);
        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        endProcess.add(triple3);
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));
        endProcess.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.PROCESS_DONE, endProcess, 0));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetIterationProcess() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_iteration_process.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstIteration = new ArrayList<Statement>();
        ArrayList secondIteration = new ArrayList<Statement>();
        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));


        firstIteration.add(triple1);
        firstIteration.add(triple2);

        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));

        secondIteration.add(triple3);
        secondIteration.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, firstIteration, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, secondIteration, 1));

        ArrayList endProcess = new ArrayList<Statement>();

        endProcess.add(triple1);
        endProcess.add(triple2);
        endProcess.add(triple3);
        endProcess.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.PROCESS_DONE, endProcess, 0));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetIterationTriple() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_iteration_triple.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstIteration = new ArrayList<Statement>();
        ArrayList secondIteration = new ArrayList<Statement>();
        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));


        firstIteration.add(triple1);
        firstIteration.add(triple2);

        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));

        secondIteration.add(triple3);
        secondIteration.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, firstIteration, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.ITERATION_DONE, secondIteration, 1));

        ArrayList firstTriple = new ArrayList<Statement>();
        ArrayList secondTriple = new ArrayList<Statement>();
        ArrayList thirdTriple = new ArrayList<Statement>();
        ArrayList fourthTriple = new ArrayList<Statement>();

        firstTriple.add(triple1);
        secondTriple.add(triple2);
        thirdTriple.add(triple3);
        fourthTriple.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, firstTriple, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, secondTriple, 1));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, thirdTriple, 2));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, fourthTriple, 3));


        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetTripleProcess() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_triple_process.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstTriple = new ArrayList<Statement>();
        ArrayList secondTriple = new ArrayList<Statement>();
        ArrayList thirdTriple = new ArrayList<Statement>();
        ArrayList fourthTriple = new ArrayList<Statement>();

        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        firstTriple.add(triple1);
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));
        secondTriple.add(triple2);
        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        thirdTriple.add(triple3);
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));
        fourthTriple.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, firstTriple, 0));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, secondTriple, 1));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, thirdTriple, 2));
        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.TRIPLE_DONE, fourthTriple, 3));

        ArrayList endProcess = new ArrayList<Statement>();

        endProcess.add(triple1);
        endProcess.add(triple2);
        endProcess.add(triple3);
        endProcess.add(triple4);

        LogicalTargetBus.getEventBus().register(new BufferTester(BusEventTypes.PROCESS_DONE, endProcess, 0));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetIterationFunction() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_iteration_function.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example-function.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    class BufferTester {

        private Enum flushEvent;
        private  List<Statement> expectedStatements;
        private int iteration;
        private int counter;

        BufferTester(Enum flushEvent, List<Statement> expectedStatements, int iteration) {
            this.flushEvent = flushEvent;
            this.expectedStatements = expectedStatements;
            this.iteration = iteration;
            counter = 0;
        }

        BufferTester(Enum flushEvent, List<Statement> expectedStatements) {
            this(flushEvent, expectedStatements, 0);
        }

        @Subscribe
        public void recordFlushEvent(Enum e) {
            if (e == flushEvent
                    && (flushEvent != BusEventTypes.ITERATION_DONE || (flushEvent == BusEventTypes.ITERATION_DONE && counter == iteration))
                    && (flushEvent != BusEventTypes.TRIPLE_DONE || (flushEvent == BusEventTypes.TRIPLE_DONE && counter == iteration))) {

                List<Statement> statements = new ArrayList<>();

                if (e == BusEventTypes.ITERATION_DONE) {
                    int i = 0;

                    while (i < LogicalTargetBus.getSubscribers().size() && !(LogicalTargetBus.getSubscribers().get(i) instanceof IterationBuffer)) {
                        i ++;
                    }

                    if (i < LogicalTargetBus.getSubscribers().size()) {
                        IterationBuffer ib = (IterationBuffer) LogicalTargetBus.getSubscribers().get(i);
                        statements = ib.getStatements();
                    }
                }

                if (e == BusEventTypes.TRIPLE_DONE) {
                    int i = 0;

                    while (i < LogicalTargetBus.getSubscribers().size() && !(LogicalTargetBus.getSubscribers().get(i) instanceof TripleBuffer)) {
                        i ++;
                    }

                    if (i < LogicalTargetBus.getSubscribers().size()) {
                        TripleBuffer tb = (TripleBuffer) LogicalTargetBus.getSubscribers().get(i);
                        statements = tb.getStatements();
                    }
                }

                if (e == BusEventTypes.PROCESS_DONE) {
                    int i = 0;

                    while (i < LogicalTargetBus.getSubscribers().size() && !(LogicalTargetBus.getSubscribers().get(i) instanceof ProcessBuffer)) {
                        i ++;
                    }

                    if (i < LogicalTargetBus.getSubscribers().size()) {
                        ProcessBuffer pb = (ProcessBuffer) LogicalTargetBus.getSubscribers().get(i);
                        statements = pb.getStatements();
                    }
                }

                for (int i = 0; i < expectedStatements.size(); i ++ ) {
                    assertEquals(expectedStatements.get(i), statements.get(i));
                }
            }

            if (e == BusEventTypes.ITERATION_DONE && flushEvent == BusEventTypes.ITERATION_DONE) {
                counter ++;
            }

            if (e == BusEventTypes.TRIPLE_DONE && flushEvent == BusEventTypes.TRIPLE_DONE) {
                counter ++;
            }
        }
    }

    public void testExampleLogicalTargetTripleHTTP() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_triple_http.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        ArrayList firstTriple = new ArrayList<Statement>();
        ArrayList secondTriple = new ArrayList<Statement>();
        ArrayList thirdTriple = new ArrayList<Statement>();
        ArrayList fourthTriple = new ArrayList<Statement>();

        ValueFactory factory = SimpleValueFactory.getInstance();
        Statement triple1 = factory.createStatement(factory.createIRI("http://example.org/1"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple2 = factory.createStatement(factory.createIRI("http://example.org/1"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("50 50"));
        Statement triple3 = factory.createStatement(factory.createIRI("http://example.org/2"), RDF.TYPE, factory.createIRI("http://www.example.com/Test"));
        Statement triple4 = factory.createStatement(factory.createIRI("http://example.org/2"), factory.createIRI("http://www.w3.org/2003/01/geo/wgs84_pos#lon"), factory.createLiteral("60"));

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }

    public void testExampleLogicalTargetIterationHTTP() {
        URL fileToRMLFile = getClass().getResource("/exampleLogicalTarget/mapping_iteration_http.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/exampleLogicalTarget/example.output.ttl");
        String[] triplesMap = {"http://example.com/test#Person_TemplateMapping"};

        assertTrue(desiredOutput(fileToOutputFile).isEqualTo(assertMap(fileToRMLFile, null, triplesMap)));
    }
}
