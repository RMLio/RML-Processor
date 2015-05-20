package be.ugent.mmlab.rml.input.extractor;

import be.ugent.mmlab.rml.input.model.InputSource;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.Set;
import org.openrdf.model.Resource;

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
    //InputStream getInputStream (String source, TriplesMap triplesMap);
    
    /**
     *
     * @return
     */
    public Set<InputSource> extractInput (RMLSesameDataSet rmlMappingGraph, Resource resource);
    
    
}
