/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.input.concrete;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.extractor.input.AbstractInputExtractor;
import be.ugent.mmlab.rml.extractor.input.InputExtractor;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */
public class ApiExtractor extends AbstractInputExtractor implements InputExtractor {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(LocalFileExtractor.class);

    @Override
    public InputStream getInputStream(String source, TriplesMap triplesMap) {
        InputStream input = null;

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(source).openConnection();
            con.setRequestMethod("HEAD");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = new URL(source).openStream();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LocalFileExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return input;
    }

    @Override
    public String getInputSource(String reference, TriplesMap map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String extractInput(RMLSesameDataSet rmlMappingGraph, Resource resource) {
        
        URI predicate = rmlMappingGraph.URIref("http://www.w3.org/ns/hydra/core#template");
        
         List<Statement> statements = rmlMappingGraph.tuplePattern(
                        (Resource) resource, predicate, null);
         
         return statements.get(0).getObject().stringValue();
         
    }
    
}
