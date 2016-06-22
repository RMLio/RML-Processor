package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.core.RMLExecutionEngine;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.std.StdConditionSubjectMap;
import be.ugent.mmlab.rml.processor.ConditionProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.StdConditionProcessor;
import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.Levenshtein;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performer to do joins with rr:joinCondition
 *
 * @author mielvandersande, andimou
 */
//TODO: Remove ConditionalJoinRMLPerformer, never used
public class ConditionalJoinRMLPerformer extends NodeRMLPerformer{
    private TermMapProcessor termMapProcessor ;
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(ConditionalJoinRMLPerformer.class);
    
    private HashMap<String, String> conditions;
    private Resource subject;
    private URI predicate;
    private Resource graph;
    private Resource metric;

    public ConditionalJoinRMLPerformer(
            RMLProcessor processor, HashMap<String, String> conditions, 
            Resource subject, URI predicate, Resource graph) {
        super(processor);
        this.conditions = conditions;
        this.subject = subject;
        this.predicate = predicate;
    }
    
    public ConditionalJoinRMLPerformer(
            RMLProcessor processor, HashMap<String, String> conditions, 
            Resource subject, URI predicate, Resource graph, Resource metric) {
        super(processor);
        this.conditions = conditions;
        this.subject = subject;
        this.predicate = predicate;
        this.graph = graph;
        this.metric = metric;
    }
    
    public ConditionalJoinRMLPerformer(
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
    public boolean perform(Object node, RMLDataset dataset, TriplesMap map, 
    String[] exeTriplesMap, Map<String, String> parameters, boolean pomExecution) {
        Value object;
        boolean result = false;

        //iterate the conditions, execute the expressions and compare both values
        if(conditions != null){
            boolean flag = true;

            iter: for (String expr : conditions.keySet()) {   
                String cond = conditions.get(expr);
                
                TermMapProcessorFactory factory = new ConcreteTermMapFactory();
                this.termMapProcessor = 
                factory.create(map.getLogicalSource().getReferenceFormulation());
                
                List<String> values = termMapProcessor.extractValueFromNode(node, expr);
                
                if(values.size() == 0)
                    flag = false;
                
                flag = equalityMetric(values, cond);
                if(!flag)
                    break iter;
            }
            
            if (flag) {
                boolean condResult = true;
                if (map.getSubjectMap().getClass().getSimpleName().equals("StdConditionSubjectMap")) {
                    log.debug("Conditional Subject Map");
                    StdConditionSubjectMap condSubMap =
                            (StdConditionSubjectMap) map.getSubjectMap();
                    Set<Condition> conditions = condSubMap.getConditions();
                    ConditionProcessor condProcessor = new StdConditionProcessor();
                    condResult = condProcessor.processConditions(node, termMapProcessor, conditions);
                }
                if (condResult) {
                    object = processor.processSubjectMap(this.processor, dataset,
                            map, map.getSubjectMap(), node, exeTriplesMap);
                    if (subject != null && object != null) {
                        List<Statement> triples =
                                dataset.tuplePattern(subject, predicate, object);
                        if (triples.size() == 0) {
                            dataset.add(subject, predicate, object, graph);
                            log.debug("Subject " + subject
                                    + " Predicate " + predicate
                                    + " Object " + object.toString());
                            result = true;
                        }

                        if (exeTriplesMap != null) {
                            RMLExecutionEngine executionEngine =
                                    new RMLExecutionEngine(exeTriplesMap);
                            pomExecution = executionEngine.
                                    checkExecutionList(map, exeTriplesMap);
                        }

                        if (!pomExecution
                                || map.getSubjectMap().getTermType().equals(TermType.BLANK_NODE)) {
                            log.debug("Nested performer is called");
                            NestedRMLPerformer nestedPerformer =
                                    new NestedRMLPerformer(processor);
                            nestedPerformer.perform(
                                    node, dataset, (Resource) object, map, exeTriplesMap, true);
                        }
                    } else {
                        log.debug("Triple for Subject " + subject
                                + " Predicate " + predicate
                                + "Object " + object
                                + "was not created");
                    }
                }
            }
        }
        return result;
    }
    
    //TODO: Move that in a separate class
    private boolean equalityMetric(List<String> values, String cond) {
        
        boolean flag = false ;
        //TODO: check if it stops as soon as it finds something
        for (String value : values) {
            //log.debug("Value " + value + " is compared to " + cond);

            if (value == null) {
                log.debug("Null value...");
                flag = false;
                break;
            } else if (this.metric == null) {
                //log.debug("No metric...");
                if((value.equals(cond)))
                    flag = true;
                break;
            } else if (this.metric.stringValue().equals(
                    "http://semweb.mmlab.be/ns/crml#Jaccard")){
                log.debug("Processing Jaccard distance...");
                Jaccard j = new Jaccard();
                double distance = j.distance(value, cond);
                if(distance == 0)
                    flag = true;
                break;
            } else if(this.metric.stringValue().equals(
                    "http://semweb.mmlab.be/ns/crml#Levenstein")){
                log.debug("Processing Levenshtein distance..");
                Levenshtein l = new Levenshtein();
                double distance1 = l.distance(value, cond);
                if(distance1 == 0)
                    flag = true;
                break;
            }
        }
        return flag;
    }
}