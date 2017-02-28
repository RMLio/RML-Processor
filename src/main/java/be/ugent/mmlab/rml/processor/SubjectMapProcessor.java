package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import org.eclipse.rdf4j.model.Resource;

/**
 *
 * @author andimou
 */
public interface SubjectMapProcessor {
    
    public Resource processSubjectMap(
            RMLDataset dataset, SubjectMap subjectMap, Object node, RMLProcessor processor);
    
    public void processSubjectTypeMap(RMLDataset dataset, 
            Resource subject, TriplesMap map, Object node);
}
