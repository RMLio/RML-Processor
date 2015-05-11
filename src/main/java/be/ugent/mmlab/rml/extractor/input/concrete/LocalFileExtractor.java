/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.input.concrete;

import be.ugent.mmlab.rml.Input.API;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.extractor.input.InputExtractor;
import static be.ugent.mmlab.rml.core.RMLEngine.isLocalFile;
import be.ugent.mmlab.rml.extractor.input.AbstractInputExtractor;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;

/**
 *
 * @author andimou
 */
public class LocalFileExtractor extends AbstractInputExtractor implements InputExtractor {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(LocalFileExtractor.class);
    
    
    /**
     *
     * @param source
     * @param triplesMap
     * @return
     * @throws IOException
     */
    @Override
    public InputStream getInputStream(String source, TriplesMap triplesMap) {
        InputStream input = null;

            source = getInputSource(source, triplesMap);
            try {
                input = new FileInputStream(new File(source));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LocalFileExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }

        return input;
    }
    
    /**
     *
     * @param source
     * @param triplesMap
     * @return
     */
    @Override
    public String getInputSource(String source, TriplesMap triplesMap) {

        if (!isLocalFile(source)) {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(source).openConnection();
                con.setRequestMethod("HEAD");
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return source;
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LocalFileExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (isLocalFile(source)) {
            try {
                File file = new File(new File(source).getAbsolutePath());

                if (!file.exists()) {
                    if (RMLEngine.class.getResource(triplesMap.getLogicalSource().getIdentifier()) == null) {
                        source = triplesMap.getLogicalSource().getIdentifier();
                        file = new File(new File(source).getAbsolutePath());
                    } else {
                        source = RMLEngine.class.getResource(triplesMap.getLogicalSource().getIdentifier()).getFile();
                        file = new File(new File(source).getCanonicalPath());
                    }
                    if (!file.exists()) {
                        log.error("[LocalFileExtractor] Input file not found. ");
                    }
                }
                source = file.toString();
                //input = new FileInputStream(file);
            } catch (IOException ex) {
                Logger.getLogger(RMLEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            log.info("[LocalFileExtractor] Input stream was not possible.");
            return null;
        }
        return source;
    }
    
    
    @Override
    public String extractInput(RMLSesameDataSet rmlMappingGraph, Resource resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
