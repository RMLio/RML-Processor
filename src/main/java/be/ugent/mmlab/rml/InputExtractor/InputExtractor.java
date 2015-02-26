/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.InputExtractor;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.InputStream;

/**
 *
 * @author andimou
 */
public interface InputExtractor {
    
    InputStream getInputStream (String source, TriplesMap triplesMap);
    
    boolean isLocalFile(String source);

    public String getInputSource(String reference, TriplesMap map);
    
}
