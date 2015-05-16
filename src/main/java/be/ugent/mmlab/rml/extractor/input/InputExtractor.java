/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.input;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.InputStream;
import java.util.Set;
import org.openrdf.model.Resource;

/**
 *
 * @author andimou
 */
public interface InputExtractor {
    
    InputStream getInputStream (String source, TriplesMap triplesMap);
    
    /**
     *
     * @return
     */
    public String extractInput (RMLSesameDataSet rmlMappingGraph, Resource resource);
    
    //boolean isLocalFile(String source);

    public String getInputSource(String reference, TriplesMap map);

    public Set<String> extractStringTemplate(String source);
    
}
