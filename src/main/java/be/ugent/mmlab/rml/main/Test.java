package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.model.RMLMapping;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author mielvandersande
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //String fileToR2RMLFile = "/Users/mielvandersande/Desktop/Projects/USC-ISI/Karma/R2RML/Example/documents-export-2013-10-14/example.rml.ttl";
            //String fileToR2RMLFile = "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/iMinds_Project.rml.ttl";
           String fileToR2RMLFile = "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/contactData.rml.ttl";

            RMLEngine.fileMap = new HashMap<String, String>();
            //RMLEngine.fileMap.put("ProjectsPartners.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/ProjectsPartners.csv");
            //RMLEngine.fileMap.put("ProjectsTimesheets.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/ProjectsTimesheets.csv");
            //RMLEngine.fileMap.put("ResearchGroups.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/ResearchGroups.csv");
            //RMLEngine.fileMap.put("ResearchGroupsUniversities.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/ResearchGroupsUniversities.csv");
            //RMLEngine.fileMap.put("ResearchGroupsUniversities.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/HelloWorldPerson.csv");
            RMLEngine.fileMap.put("HelloWorldPerson.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/HelloWorldPerson.csv");
            RMLEngine.fileMap.put("HelloWorldOrganisation.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/HelloWorldPerson.csv");
            RMLEngine.fileMap.put("HelloWorldOrganisationRelation.csv", "/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/HelloWorldPerson.csv");
            
            RMLMapping mapping = RMLMappingFactory.extractRMLMapping(fileToR2RMLFile);

            RMLEngine engine = new RMLEngine();
            SesameDataSet output = engine.runRMLMapping(mapping, "http://iminds.be/");
            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/output_Project.rdf", RDFFormat.RDFXML);
            //output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/iMinds/output_Project.ttl", RDFFormat.TURTLE);
            output.dumpRDF("/home/andimou/Documents/RML/andimou/RMLProcessor3/RMLProcessor/src/test/resources/ContactData/contactData.ttl", RDFFormat.TURTLE);
            //output.dumpRDF(System.out, RDFFormat.TURTLE);
        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidR2RMLStructureException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidR2RMLSyntaxException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (R2RMLDataError ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
