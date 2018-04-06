package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.std.StdConditionPredicateMap;
import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class PredicateMapProcessor {
    private TermMapProcessor termMapProcessor ;
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(PredicateMapProcessor.class);
    
    public PredicateMapProcessor(TriplesMap map){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(
                map.getLogicalSource().getReferenceFormulation());
    }
    
    public PredicateMapProcessor(TriplesMap map, RMLProcessor processor){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(
                map.getLogicalSource().getReferenceFormulation(), processor);
    }
      
    /**
     * process a predicate map
     *
     * @param predicateMap
     * @param node
     * @return the uri of the extracted predicate
     */
    
    public List<IRI> processPredicateMap(PredicateMap predicateMap, Object node) {
        List<IRI> uris = new ArrayList<>();
        boolean result = false;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        
        if (predicateMap.getClass().getSimpleName().equals("StdConditionPredicateMap")) {
            log.debug("Conditional Predicate Map");
            StdConditionPredicateMap condPreMap =
                    (StdConditionPredicateMap) predicateMap;
            Set<Condition> conditions = condPreMap.getConditions();
            ConditionProcessor condProcessor = new StdConditionProcessor();
            result = condProcessor.processConditions(node, termMapProcessor, conditions);
        }
        else{
            result = true;
        }
        
        if (result) {
            // Get the value
            List<String> values =
                    this.termMapProcessor.processTermMap(predicateMap, node);

            for (String value : values) {
                //TODO: add better control
                if (value.startsWith("www.")) {
                    value = "http://" + value;
                }
                uris.add(vf.createIRI(value));
            }
        }
        //return the uri
        return uris;
    }

}
