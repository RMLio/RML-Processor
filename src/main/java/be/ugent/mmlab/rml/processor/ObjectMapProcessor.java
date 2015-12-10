package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.BindingCondition;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.std.BindingReferencingObjectMap;
import be.ugent.mmlab.rml.input.processor.AbstractInputProcessor;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RDFTerm.ObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.std.StdConditionObjectMap;
import be.ugent.mmlab.rml.performer.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.performer.JoinRMLPerformer;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.performer.SimpleReferencePerformer;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class ObjectMapProcessor {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(ObjectMapProcessor.class);
    
    private TermMapProcessor termMapProcessor ;
    
    public ObjectMapProcessor(TriplesMap map){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = 
                factory.create(map.getLogicalSource().getReferenceFormulation());
    }
    
    
    public void processPredicateObjectMap_ObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node) {
        
        Set<ObjectMap> objectMaps = pom.getObjectMaps();
        for (ObjectMap objectMap : objectMaps) {
            //Get the one or more objects returned by the object map
            List<Value> objects = processObjectMap(objectMap, node);
            
            if(objectMap.getClass().getSimpleName().equals("StdConditionObjectMap")){
                log.debug("Conditional Object Map");
                StdConditionObjectMap tmp = (StdConditionObjectMap) objectMap;
                Set<Condition> conditions = tmp.getConditions();
                for (Condition condition : conditions){
                    String expression = condition.getCondition();
                    if(expression.contains("toUppercase")){
                        //TODO: Add body
                    }
                }
            }
            else
                log.debug("Simple Object Map");
            
            //smth = objectMap.processBooleanConditions(node, objectMap);
            if (objects != null) {
                for (Value object : objects) {
                    if (object.stringValue() != null) {
                        Set<GraphMap> graphs = pom.getGraphMaps();
                        if (graphs.isEmpty() && subject != null) {
                            List<Statement> triples = 
                                    dataset.tuplePattern(subject, predicate, object);
                            if(triples.size() == 0){
                                dataset.add(subject, predicate, object); 
                            }
                        } else {
                            for (GraphMap graph : graphs) {
                                Resource graphResource = new URIImpl(
                                        graph.getConstantValue().toString());
                                dataset.add(subject, predicate, object, graphResource);
                                
                            }
                        }

                    }
                }
            } else {
                log.debug("No object created. No triple will be generated.");
            }
        }
    }
    
    public List<Value> processObjectMap(ObjectMap objectMap, Object node) {
        //A Term map returns one or more values (in case expression matches more)
        
        List<String> values = this.termMapProcessor.processTermMap(objectMap, node);
        List<Value> valueList = new ArrayList<>();
        for (String value : values) {
            valueList = this.termMapProcessor.applyTermType(value, valueList, objectMap);
        }
        
        return valueList;
    }
    
    public void processPredicateObjectMap_RefObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node, 
            TriplesMap map, String[] exeTriplesMap) {
        String template ;
        Map<String, String> parameters = null;
        
        Set<ReferencingObjectMap> referencingObjectMaps = 
                pom.getReferencingObjectMaps();
        for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
            if(referencingObjectMap.getParentTriplesMap().getLogicalSource() == null){
                continue;}
            
            Set<BindingCondition> bindingConditions = null;
            Set<JoinCondition> joinConditions = 
                    referencingObjectMap.getJoinConditions();
            
            TriplesMap parentTriplesMap = 
                    referencingObjectMap.getParentTriplesMap();
            
            template = parentTriplesMap.
                    getLogicalSource().getSource().getTemplate();
            
            if(referencingObjectMap.getClass().getSimpleName().
                    equals("BindingReferencingObjectMap")){
                log.debug("Processing Referencing Object Map "
                        + "with Binding Condition..");
                BindingReferencingObjectMap bindingReferencingObjectMap = 
                        (BindingReferencingObjectMap) referencingObjectMap;
                bindingConditions =
                        bindingReferencingObjectMap.getBindingConditions();
                parameters = 
                        processBindingConditions(node, bindingConditions);
            }

            //Create the processor based on the parent triples map to perform the join
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            QLVocabulary.QLTerm referenceFormulation = 
                    parentTriplesMap.getLogicalSource().getReferenceFormulation();

            SourceProcessor inputProcessor = new AbstractInputProcessor();
                       
            InputStream input = inputProcessor.getInputStream(
                    parentTriplesMap.getLogicalSource(), parameters);
            
            RMLProcessor processor = factory.create(referenceFormulation);
            RMLPerformer performer = null;
            
            //different Logical Source and no Conditions
            if (joinConditions.isEmpty()
                    & !parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())
                    & bindingConditions == null) {
                log.debug("Referencing Object Map with Logical Source "
                        + "without join and binding conditions.");
                performer = new JoinRMLPerformer(processor, subject, predicate);
                processor.execute(dataset, parentTriplesMap, performer, input, 
                        exeTriplesMap, false);
            }
            
            //different Logical Source AND 
            //no join Conditions but with Binding Conditions
            if (joinConditions.isEmpty()
                    & !parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())
                    & (bindingConditions != null)) {
                log.debug("Referencing Object Map with Logical Source "
                        + "without join conditions but with bind conditions.");
                performer = new JoinRMLPerformer(processor, subject, predicate);
                processor.execute(dataset, parentTriplesMap, performer, input, 
                        exeTriplesMap, true);
            }
            
            //same Logical Source and no Conditions
            else if (joinConditions.isEmpty()
                    & parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())) {
                
                log.debug("Referencing Object Map with Logical Source without conditions.");
                performer = new SimpleReferencePerformer(processor, subject, predicate);
                
                if ((parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().
                            equals("CSV"))
                        || (parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().
                            equals("XLSX"))
                        || (parentTriplesMap.getLogicalSource().getIterator().
                            equals(map.getLogicalSource().getIterator()))
                    ) {
                    log.debug("Tabular-structured Referencing Object Map "
                            + "or Hierarchical-structured Referencing Object Map "
                            + "with the same iterator");
                    performer.perform(
                            node, dataset, parentTriplesMap, exeTriplesMap, false);
                } else {
                    log.debug("Hierarchical-structured Referencing Object Map");
                    String expression = handleRelevantExpression(map, parentTriplesMap);
 
                    processor.execute_node(
                            dataset, expression, parentTriplesMap, 
                            performer, node, null, exeTriplesMap, false);
                }
            } //Conditions
            else {
                log.debug("Referencing Object Map with Logical Source with conditions.");
                //Build a join map where
                //  key: the parent expression
                //  value: the value extracted from the child
                processJoinConditions(node, performer, processor, subject, 
                        predicate, dataset, input, parentTriplesMap, 
                        joinConditions, exeTriplesMap);
            }
        }
    }
    
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
    
    public void processJoinConditions(Object node, RMLPerformer performer, 
            RMLProcessor processor, Resource subject, URI predicate, 
            RMLDataset dataset, InputStream input, TriplesMap parentTriplesMap, 
            Set<JoinCondition> joinConditions, String[] exeTriplesMap) {
        HashMap<String, String> joinMap = new HashMap<>();
        
        for (JoinCondition joinCondition : joinConditions) {
            if(joinCondition.getChild() == null)
                continue;
            
            List<String> childValues = termMapProcessor.extractValueFromNode(
                    node, joinCondition.getChild());
            //Allow multiple values as child - 
            //fits with RML's definition of multiple Object Maps
            for (String childValue : childValues) { 
                joinMap.put(joinCondition.getParent(), childValue);
                if (joinMap.size() == joinConditions.size()) {
                    performer = new ConditionalJoinRMLPerformer(
                            processor, joinMap, subject, predicate);
                    processor.execute(dataset, parentTriplesMap, performer, 
                            input, exeTriplesMap, false);
                }
            }
        }
    }
    
    private String handleRelevantExpression(
            TriplesMap map, TriplesMap parentTriplesMap) {
        int end = map.getLogicalSource().getIterator().length();
        String expression = "";
        //TODO:merge it with the performer's switch-case
        switch (parentTriplesMap.getLogicalSource().getReferenceFormulation().toString()) {
            case "XPath":
                expression =
                        parentTriplesMap.getLogicalSource()
                        .getIterator().toString().substring(end);
                break;
            case "JSONPath":
                expression =
                        parentTriplesMap.getLogicalSource().
                        getIterator().toString().substring(end + 1);
                break;
            case "CSS3":
                expression =
                        parentTriplesMap.getLogicalSource().
                        getIterator().toString().substring(end);
                break;
        }
        return expression;
    }

}
