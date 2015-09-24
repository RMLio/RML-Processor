package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * RML Processor
 * 
 * Performer to do joins without any join conditions
 *
 * @author mielvandersande, andimou
 */
public class JoinRMLPerformer extends NodeRMLPerformer{
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(JoinRMLPerformer.class);
    
    private Resource subject;
    private URI predicate;

    public JoinRMLPerformer(RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }

    /**
     * Compare expressions from join to complete it
     * 
     * @param node current object in parent iteration
     * @param dataset
     * @param map 
     */
    @Override
    public void perform(Object node, RMLSesameDataSet dataset, 
    TriplesMap map, String[] exeTriplesMap, boolean pomExecution) {
        Value object = processor.processSubjectMap(
                dataset, map.getSubjectMap(), node);

        if (object == null){
            return;
        }       
        
        //add the join triple
        dataset.add(subject, predicate, object);

        if(pomExecution){
            NestedRMLPerformer nestedPerformer = 
                    new NestedRMLPerformer(processor);
            nestedPerformer.perform(
                    node, dataset, map, exeTriplesMap, pomExecution);
        }
    }

}
