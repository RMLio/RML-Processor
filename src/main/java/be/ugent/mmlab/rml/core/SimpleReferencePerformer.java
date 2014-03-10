package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import java.util.ArrayList;
import java.util.List;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author andimou
 */
public class SimpleReferencePerformer extends NodeRMLPerformer {
    
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);
    private Resource subject;
    private URI predicate;
    
    public SimpleReferencePerformer(RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }
    
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        List<String> values = processor.processTermMap(map.getSubjectMap(), node);
        
        log.debug("[SimpleReferencePerformer:object] " + "values " + values);

        for(String value : values){
            Resource object = new URIImpl(value);

            dataset.add(subject, predicate, object);
            log.debug("[SimpleReferencePerformer:addTriples] Subject "
                    + subject + " Predicate " + predicate + "Object " + object.toString());
        }       

        
    }

   
}
