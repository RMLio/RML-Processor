/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.input;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.InputStream;
import java.util.Set;

/**
 *
 * @author andimou
 */
public interface InputExtractor {
    
    InputStream getInputStream (String source, TriplesMap triplesMap);
    
    boolean isLocalFile(String source);

    public String getInputSource(String reference, TriplesMap map);

    public Set<String> extractStringTemplate(String source);
    
}
