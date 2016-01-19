package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import org.openrdf.model.Resource;

/**
 *
 * @author andimou
 */
public interface SubjectMapProcessor {
    
    public Resource processSubjectMap(
            RMLDataset dataset, SubjectMap subjectMap, Object node);
    
    public void processSubjectTypeMap(RMLDataset dataset, 
            Resource subject, SubjectMap subjectMap, Object node);
}
