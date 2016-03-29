package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml. model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
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

    public JoinRMLPerformer(
            RMLProcessor processor, Resource subject, URI predicate) {
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
    public void perform(Object node, RMLDataset dataset, TriplesMap map, 
    String[] exeTriplesMap, Map<String, String> parameters, boolean pomExecution) {
        Value object = processor.processSubjectMap(this.processor,
                dataset, map, map.getSubjectMap(), node, exeTriplesMap);
        
        if (object == null){
            return;
        }       
        
        List<Statement> triples =
                dataset.tuplePattern(subject, predicate, object);
        if (triples.size() == 0) {
            //add the join triple
            dataset.add(subject, predicate, object);
        }

        if(pomExecution){
            NestedRMLPerformer nestedPerformer = 
                    new NestedRMLPerformer(processor);
            nestedPerformer.perform(
                    node, dataset, map, exeTriplesMap, parameters, pomExecution);
        }
    }

}
