package be.ugent.mmlab.rml.input.processor;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.InputStream;

/**
 *
 * @author andimou
 */
public interface InputProcessor {
    /**
     *
     * @param triplesMap
     * @param source
     * @param triplesMap
     * @return
     */
    public InputStream getInputStream(TriplesMap triplesMap, String source);
    
}
