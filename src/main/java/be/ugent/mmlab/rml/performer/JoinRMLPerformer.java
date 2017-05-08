package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml. model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

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
    private IRI predicate;

    public JoinRMLPerformer(
            RMLProcessor processor, Resource subject, IRI predicate) {
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
    public boolean perform(Object node, RMLDataset dataset, TriplesMap map, 
    String[] exeTriplesMap, Map<String, String> parameters, boolean pomExecution) {
        Value object = processor.processSubjectMap(this.processor,
                dataset, map, map.getSubjectMap(), node, exeTriplesMap);
        boolean result = true;
        if (object == null){
            result = false;
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
            boolean subresult = nestedPerformer.perform(
                            node, dataset, map, exeTriplesMap, parameters, pomExecution);
            log.debug("The subresult of the nested performer is " + subresult);
        }
        return result;
    }

}
