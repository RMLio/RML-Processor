package be.ugent.mmlab.rml.input;

import be.ugent.mmlab.rml.input.model.InputSource;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author andimou
 */
public class InputMapping {
    private Collection<InputSource> inputSources;

    public InputMapping(Collection<InputSource> inputSources) {
        this.inputSources = new HashSet<InputSource>();
        this.inputSources.addAll(inputSources);
    }
    
    public Collection<InputSource> getInputSources() {
        return inputSources;
    }
    
}
