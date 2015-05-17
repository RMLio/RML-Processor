/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.input.extractor;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author andimou
 */
public interface InputExtractor {
    
    /**
     *
     * @param source
     * @param triplesMap
     * @return
     */
    InputStream getInputStream (String source, TriplesMap triplesMap);
    
    /**
     *
     * @return
     */
    public String extractInput (RMLSesameDataSet rmlMappingGraph, Resource resource);
    
    //boolean isLocalFile(String source);

    /**
     *
     * @param rmlMappingGraph
     * @param reference
     * @param map
     * @return
     */
    public List<Statement> getInput(RMLSesameDataSet rmlMappingGraph, String reference, TriplesMap map);

    /**
     *
     * @param source
     * @param triplesMap
     * @return
     */
    public String getInputSource(String source, TriplesMap triplesMap);
            
    /**
     *
     * @param source
     * @return
     */
    public Set<String> extractStringTemplate(String source);
    
}
