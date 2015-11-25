package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * RML Processor
 *
 * @author andimou
 */
public class PredicateMapProcessor {
    private TermMapProcessor termMapProcessor ;
    
    public PredicateMapProcessor(TriplesMap map){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = 
                factory.create(map.getLogicalSource().getReferenceFormulation());
    }
    
    
    /**
     * process a predicate map
     *
     * @param predicateMap
     * @param node
     * @return the uri of the extracted predicate
     */
    
    public List<URI> processPredicateMap(PredicateMap predicateMap, Object node) {
        // Get the value
        
        List<String> values = this.termMapProcessor.processTermMap(predicateMap, node);
        List<URI> uris = new ArrayList<>();
        for (String value : values) {
            //TODO: add better control
            if(value.startsWith("www."))
                value = "http://" + value;
            uris.add(new URIImpl(value));
        }
        //return the uri
        return uris;
    }

}
