package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.BindingCondition;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.std.StdConditionPredicateMap;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
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
        List<URI> uris = new ArrayList<>();
        Map<String, String> parameters;
        boolean result = false;
        
        if (predicateMap.getClass().getSimpleName().equals("StdConditionPredicateMap")) {
            log.debug("Conditional Predicate Map");
            StdConditionPredicateMap tmp =
                    (StdConditionPredicateMap) predicateMap;
            Set<Condition> conditions = tmp.getConditions();
            Set<BindingCondition> bindings = new HashSet<BindingCondition>();
            for (Condition condition : conditions) {
                String expression = condition.getCondition();
                bindings = condition.getBindingConditions();
                
                for(BindingCondition binding : bindings){
                    parameters = processBindingConditions(node, bindings);
                    String replacement = parameters.get(binding.getVariable());
                    expression = expression.replaceAll(
                        "%%" + Pattern.quote(binding.getVariable()) + "%%",
                        replacement);
                    result = processMatch(expression);
                }

            }
        }
        else{
            log.debug("Simple Predicate Map");
            result = false;
        }
        
        if (result == true) {
            // Get the value
            List<String> values =
                    this.termMapProcessor.processTermMap(predicateMap, node);

            for (String value : values) {
                //TODO: add better control
                if (value.startsWith("www.")) {
                    value = "http://" + value;
                }
                uris.add(new URIImpl(value));
            }
        }
        //return the uri
        return uris;
    }
    
    //TODO: Move it separately
    public boolean processMatch(String expression){
        expression = expression.replace("match(", "").replace(")", "");
        String[] strings = expression.split(",");
        
        if (strings != null && strings.length > 1) {
            if (strings[0].equals(strings[1].replaceAll("\"", ""))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    //TODO: Spring it!!
    //TODO: Merge it with the one from Referencing Object Map
    public Map<String, String> processBindingConditions(
            Object node, Set<BindingCondition> bindingConditions){
        Map<String, String> parameters = new HashMap<String, String>();
        for(BindingCondition bindingCondition : bindingConditions){
            List<String> childValues = termMapProcessor.
                    extractValueFromNode(node, bindingCondition.getReference());
            
            for (String childValue : childValues) {    
                parameters.put(
                    bindingCondition.getVariable(), childValue);
            }
        }
        
        return parameters;
    }

}
