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

        RMLEngine.getFileMap().put("example.xml", getClass().getResource("/example1/example.xml").getFile());
        RMLEngine.getFileMap().put("Airport.csv", getClass().getResource("/example3/Airport.csv").getFile());
        RMLEngine.getFileMap().put("Venue.json", getClass().getResource("/example3/Venue.json").getFile());
        RMLEngine.getFileMap().put("Venue4.json", getClass().getResource("/example4/Venue.json").getFile());
        RMLEngine.getFileMap().put("Transport.xml", getClass().getResource("/example3/Transport.xml").getFile());
        RMLEngine.getFileMap().put("museum.json", getClass().getResource("/examplePaper2/museum.json").getFile());
        //RMLEngine.getFileMap().put("artist.xml", getClass().getResource("/examplePaper2/artist.xml").getFile());
        RMLEngine.getFileMap().put("moon-walkers.csv", getClass().getResource("/example5/moon-walkers.csv").getFile());

        //RMLEngine.getFileMap().put("ProjectsPartners.csv", getClass().getResource("/iMinds/ProjectsPartners.csv").getFile());
        //RMLEngine.getFileMap().put("ProjectsTimesheets.csv", getClass().getResource("/iMinds/ProjectsTimesheets.csv").getFile());
        //RMLEngine.getFileMap().put("ResearchGroups.csv", getClass().getResource("/iMinds/ResearchGroups.csv").getFile());
        //RMLEngine.getFileMap().put("HelloWorldPerson.csv", getClass().getResource("/ContactData/HelloWorldPerson.csv").getFile());
        //RMLEngine.getFileMap().put("HelloWorldOrganisation.csv", getClass().getResource("/ContactData/HelloWorldOrganisation2.csv").getFile());
        //RMLEngine.getFileMap().put("HelloWorldOrganisationRelation.csv", getClass().getResource("/ContactData/HelloWorldOrganisationRelation.csv").getFile());
        //RMLEngine.getFileMap().put("contactDataFullEx.csv", getClass().getResource("/ContactData/contactDataFullEx.csv").getFile());
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
    //ESWC14 paper's example test

    public void testExample5() {
        URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }
    /*
     public void testExamplePaper2() {
     URL fileToRMLFile = getClass().getResource("/examplePaper2/museum-model.rml_1.ttl");
     URL fileToOutputFile = getClass().getResource("/examplePaper2/museum.output.ttl");
     //URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
     //URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
     assertTrue(assertMap(fileToRMLFile, fileToOutputFile));   
     }*/
    //iMinds mappings
    /*public void testiMinds() {
     URL fileToRMLFile = getClass().getResource("/iMinds/iMinds.rml.ttl");
     URL fileToOutputFile = getClass().getResource("/iMinds/iMinds.output.ttl");
     assertTrue(assertMap(fileToRMLFile, fileToOutputFile));   
     }*/

    public void testEWI_ContactData() {
        //URL fileToRMLFile = getClass().getResource("/ContactData/contactData.rml.ttl");
        //URL fileToOutputFile = getClass().getResource("/ContactData/contactData.output.ttl");
        URL fileToRMLFile = getClass().getResource("/ContactData/contactDataFullEx.rml.ttl");
        URL fileToOutputFile = getClass().getResource("/ContactData/contactDataFullEx.output.ttl");
        assertTrue(assertMap(fileToRMLFile, fileToOutputFile));
    }

    /*public void testExamplePaper2() {
     URL fileToRMLFile = getClass().getResource("/examplePaper2/museum-model.rml_2.ttl");
     URL fileToOutputFile = getClass().getResource("/examplePaper2/museum.output.ttl");
     //URL fileToRMLFile = getClass().getResource("/example5/museum-model.rml.ttl");
     //URL fileToOutputFile = getClass().getResource("/example5/museum.output.ttl");
     assertTrue(assertMap(fileToRMLFile, fileToOutputFile));   
     }*/
    //iMinds mappings
    /*public void testiMinds() {
     URL fileToRMLFile = getClass().getResource("/iMinds/iMinds.rml.ttl");
     URL fileToOutputFile = getClass().getResource("/iMinds/iMinds.output.ttl");
     assertTrue(assertMap(fileToRMLFile, fileToOutputFile));   
     }*/
    /*public void testiMinds_Projects() {
     URL fileToRMLFile = getClass().getResource("/iMinds/iMinds_Project.rml.ttl");
     URL fileToOutputFile = getClass().getResource("/iMinds/iMinds_Project.output.ttl");
     assertTrue(assertMap(fileToRMLFile, fileToOutputFile));   
     }*/
    private boolean assertMap(URL mappingURL, URL outputURL) {
        try {
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(mappingURL.getFile());

            RMLEngine engine = new RMLEngine();
            //SesameDataSet output = engine.runRMLMapping(mapping, "http://iminds.be/");
            //SesameDataSet output = engine.runRMLMapping(mapping, "http://ewi.mmlab.be/ecd/");
            SesameDataSet output = engine.runRMLMapping(mapping, "http://example.com");

            output.dumpRDF(System.out, RDFFormat.TURTLE);

            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/examplePaper2/outputPaper2.ttl", RDFFormat.TURTLE);
            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/output.rdf", RDFFormat.RDFXML);
            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/output.ttl", RDFFormat.TURTLE);
            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/contactDataFullEx.ttl", RDFFormat.TURTLE);

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
