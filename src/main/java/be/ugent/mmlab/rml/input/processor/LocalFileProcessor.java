package be.ugent.mmlab.rml.input.processor;

import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.LogManager;

/**
 *
 * @author andimou
 */
public class LocalFileProcessor extends AbstractInputProcessor {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(LocalFileProcessor.class);
    
    @Override
     public InputStream getInputStream(TriplesMap triplesMap, String source) {
        InputStream input = null;

        try {
            File file = new File(new File(source).getAbsolutePath());

            if (!file.exists()) {
                file = new File(new File(source).getCanonicalPath());

                if (!file.exists()) {
                    log.error(
                            Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                            + "Input file not found. ");
                }
            }
            input = new FileInputStream(file);
        } catch (IOException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
        }

        return input;
    }
     
     
     /**
     *
     * @param source
     * @param triplesMap
     * @return
     */
    public String getInputSource(String source, TriplesMap triplesMap) {
        
        //string input source
        if (isLocalFile(source)) {
            try {
                File file = new File(new File(source).getAbsolutePath());

                if (!file.exists()) {
                    if (RMLEngine.class.getResource(triplesMap.getLogicalSource().getSource()) == null) {
                        source = triplesMap.getLogicalSource().getSource();
                        file = new File(new File(source).getAbsolutePath());
                    } else {
                        source = RMLEngine.class.getResource(triplesMap.getLogicalSource().getSource()).getFile();
                        file = new File(new File(source).getCanonicalPath());
                    }
                    if (!file.exists()) {
                        log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                                + "Input file not found. ");
                    }
                }
                source = file.toString();
                //input = new FileInputStream(file);
            } catch (IOException ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
            }
        } else {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " 
                    + "Input stream was not possible.");
            return null;
        }
        return source;
    }
    
    public static boolean isLocalFile(String source) {
        try {
            new URL(source);
            return false;
        } catch (MalformedURLException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
            return true;
        }
    }
}
