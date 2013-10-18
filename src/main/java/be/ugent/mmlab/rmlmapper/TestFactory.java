/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rmlmapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author mielvandersande
 */
public class TestFactory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String fileToR2RMLFile = "/Users/mielvandersande/Desktop/Projects/USC-ISI/Karma/R2RML/Example/documents-export-2013-10-14/example.rml.ttl";

            R2RMLMapping mapping = RMLMappingFactory.extractR2RMLMapping(fileToR2RMLFile);
            
            
            
            System.out.println(mapping);
            
            
        } catch (InvalidR2RMLStructureException ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidR2RMLSyntaxException ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (R2RMLDataError ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
