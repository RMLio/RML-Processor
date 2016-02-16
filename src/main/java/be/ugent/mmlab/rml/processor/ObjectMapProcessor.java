package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;

/**
 *
 * @author andimou
 */
public interface ObjectMapProcessor {
    
    public void processPredicateObjectMap_ObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node);
    
    public void processPredicateObjectMap_RefObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node, 
            TriplesMap map, String[] exeTriplesMap);
    
}
