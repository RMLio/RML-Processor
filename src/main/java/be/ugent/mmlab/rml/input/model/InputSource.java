package be.ugent.mmlab.rml.input.model;

import be.ugent.mmlab.rml.model.TriplesMap;

/**
 *
 * @author andimou
 */
public interface InputSource {
    
    public void addTriplesMap(TriplesMap triplesMap);
    
    public String getSource();
    
}
