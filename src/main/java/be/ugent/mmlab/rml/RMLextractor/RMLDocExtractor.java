/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.RMLextractor;

import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author andimou
 */
public class RMLDocExtractor {
    
    // Log
    private static final Logger log = LogManager.getLogger(RMLDocExtractor.class);
    
    public RMLDocExtractor(){}
    
    public RMLSesameDataSet getMappingDoc(String fileToRMLFile, RDFFormat format) {
        RMLSesameDataSet rmlMappingGraph = new RMLSesameDataSet();

        //RML document is a URI
        if (!isLocalFile(fileToRMLFile)) {
            try {
                log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "file "
                        + fileToRMLFile + " loaded from URI.");
                HttpURLConnection con = (HttpURLConnection) new URL(fileToRMLFile).openConnection();
                con.setRequestMethod("HEAD");
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        rmlMappingGraph.addURI(fileToRMLFile, RDFFormat.TURTLE);
                    } catch (Exception e) {
                        log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                + "[RMLMapping Factory:extractRMLMapping] " + e);
                    }
                }
            } catch (MalformedURLException ex) {
                java.util.logging.Logger.getLogger(RMLDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(RMLDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        else {
            try {
                rmlMappingGraph.loadDataFromFile(fileToRMLFile, RDFFormat.TURTLE);
            } catch (RepositoryException ex) {
                java.util.logging.Logger.getLogger(RMLDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(RMLDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RDFParseException ex) {
                java.util.logging.Logger.getLogger(RMLDocExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Number of R2RML triples in file "
                + fileToRMLFile + " : " + rmlMappingGraph.getSize() + " from local file");

        return rmlMappingGraph;
    }
    
    public static boolean isLocalFile(String source) {
        try {
            new URL(source);
            return false;
        } catch (MalformedURLException e) {
            return true;
        }
    }
    
}
