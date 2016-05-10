package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.BindingCondition;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.std.StdJoinConditionMetric;
import be.ugent.mmlab.rml.model.std.ConditionReferencingObjectMap;
import be.ugent.mmlab.rml.input.processor.AbstractInputProcessor;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RDFTerm.ObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.std.StdConditionObjectMap;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE;
import be.ugent.mmlab.rml.performer.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.performer.JoinRMLPerformer;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.performer.SimpleReferencePerformer;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class StdObjectMapProcessor implements ObjectMapProcessor {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(
            StdObjectMapProcessor.class.getSimpleName());
    
    protected TermMapProcessor termMapProcessor ;
    
    public StdObjectMapProcessor(TriplesMap map){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = 
                factory.create(map.getLogicalSource().getReferenceFormulation());
    }
    
    public StdObjectMapProcessor(TriplesMap map, RMLProcessor processor){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(
                map.getLogicalSource().getReferenceFormulation(), processor);
    }
    
    @Override
    public void processPredicateObjectMap_ObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node) {
        
        Set<ObjectMap> objectMaps = pom.getObjectMaps();
        
        for (ObjectMap objectMap : objectMaps) {
            boolean flag = true;
            //Get the one or more objects returned by the object map
            List<Value> objects = processObjectMap(objectMap, node);
            
            if(objectMap.getClass().getSimpleName().equals("StdConditionObjectMap")){
                StdConditionObjectMap tmp = (StdConditionObjectMap) objectMap;
                Set<Condition> conditions = tmp.getConditions();
                
                //process conditions
                ConditionProcessor condProcessor = new StdConditionProcessor();
                flag = condProcessor.processConditions(
                        node, termMapProcessor, conditions);
            }
            
            if (flag && objects != null) {
                
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
        List<Value> valueList = new ArrayList<>();
        //A Term map returns one or more values (in case expression matches more)
        
        if (!objectMap.getTermType().equals(BLANK_NODE)) {
            List<String> values = this.termMapProcessor.processTermMap(objectMap, node);
            for (String value : values) {
                valueList = this.termMapProcessor.applyTermType(value, valueList, objectMap);
            }
        }
        else {
            valueList.add(new BNodeImpl(null));
        }
        
        return valueList;
    }
    
    @Override
    public void processPredicateObjectMap_RefObjMap(
            RMLDataset dataset, Resource subject, URI predicate,
            Set<ReferencingObjectMap> referencingObjectMaps, Object node, TriplesMap map, 
            Map<String, String> parameters, String[] exeTriplesMap) {
        String template ;
       
        if (referencingObjectMaps.size() > 0) {
            log.debug("Processing Referencing Object Map...");
        }
        for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
            //TriplesMap parTrMap = referencingObjectMap.getParentTriplesMap();
            
            if (referencingObjectMap.getParentTriplesMap().getLogicalSource() == null) {
                continue;
            }
            TriplesMap parentTriplesMap =
                    referencingObjectMap.getParentTriplesMap();

            template = parentTriplesMap.
                    getLogicalSource().getSource().getTemplate();

            Set<Condition> conditions = null;
            Set<JoinCondition> joinConditions;
            Set<BindingCondition> bindingConditions = new HashSet<BindingCondition>();

            joinConditions = referencingObjectMap.getJoinConditions();
            
            if (referencingObjectMap.getClass().getSimpleName().equals(
                    "ConditionReferencingObjectMap") ) {
                
                //Retrieving Conditions
                log.debug("Condition Referencing Object Map");
                ConditionReferencingObjectMap condRefObjMap  =
                        (ConditionReferencingObjectMap) referencingObjectMap;
                conditions = condRefObjMap.getConditions();
                
                //Processing conditions
                ConditionProcessor condProcessor = new StdConditionProcessor();
                boolean result = condProcessor.processConditions(
                        node, termMapProcessor, conditions);
                
                //Processing Binding Conditions
                log.debug("Processing Conditions...");
                for (Condition condition : conditions) {
                    
                    if (condition.getClass().getSimpleName().equals("StdBindingCondition")) {
                        BindingCondition bindCondition = (BindingCondition) condition;
                        bindingConditions.add(bindCondition);
                    }
                }
                parameters = processBindingConditions(node, bindingConditions);
                
                //If conditions fail  do not proceed
                if(!result  && bindingConditions.isEmpty()) // && joinConditions.isEmpty())
                    continue;
            } else {
                log.debug("Simple Referencing Object Map");
            }
            
            //Binding Referencing Object Map
            /*if(referencingObjectMap.getClass().getSimpleName().
                    equals("BindingReferencingObjectMap")){
                log.debug("Processing Referencing Object Map "
                        + "with Binding Condition..");
                ConditionReferencingObjectMap bindingReferencingObjectMap = 
                        (ConditionReferencingObjectMap) referencingObjectMap;

            }*/
            
            log.debug("Executing Referencing Object Map....");

            //Create the processor based on the parent triples map to perform the join
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            QLVocabulary.QLTerm referenceFormulation = 
                    parentTriplesMap.getLogicalSource().getReferenceFormulation();

            SourceProcessor inputProcessor = new AbstractInputProcessor();
                       
            InputStream input = inputProcessor.getInputStream(
                    parentTriplesMap.getLogicalSource(), parameters);
            try {
                log.debug("available input " + input.available());
            } catch (IOException ex) {
                log.error("IOException " + ex);
            }
            RMLProcessor processor = factory.create(
                    referenceFormulation, parameters, parentTriplesMap);
            RMLPerformer performer = null;
            
            //different Logical Source AND no Join Conditions AND no Bind Conditions
            if (joinConditions.isEmpty()
                    & !parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())
                    & conditions == null) {
                process_difLS_noJC_noBC(performer, processor, dataset, subject, 
                        predicate, parentTriplesMap, input, exeTriplesMap);
                //continue;
            }
            
            //different Logical Source AND no join Conditions AND Binding Conditions
            if (joinConditions.isEmpty()
                    & !parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())
                    & (conditions != null)) {
                boolean result = process_difLS_noJC_withBC(performer, processor, dataset, subject, 
                        predicate, parentTriplesMap, input, exeTriplesMap);
                if (!result) {
                    log.debug("Check for falllback object maps" );
                    Set<ReferencingObjectMap> fallbackReferencingObjectMaps =
                            referencingObjectMap.getFallbackReferencingObjectMaps();
                    log.debug("Found " + fallbackReferencingObjectMaps
                            + " fallback Referencing Object Maps");
                    //Process the joins first
                    if (fallbackReferencingObjectMaps.size() > 0) {
                        ObjectMapProcessor predicateObjectProcessor =
                                new StdObjectMapProcessor(map, processor);
                        predicateObjectProcessor.processPredicateObjectMap_RefObjMap(
                                dataset, subject, predicate, fallbackReferencingObjectMaps, node,
                                map, parameters, exeTriplesMap);
                    }
                }
            }
            
            //same Logical Source and no Conditions
            else if (joinConditions.isEmpty()
                    & parentTriplesMap.getLogicalSource().getSource().getTemplate().equals(
                    map.getLogicalSource().getSource().getTemplate())) {
                
                process_sameLS_noJC(performer, processor, dataset, node, 
                        map, subject, predicate, parentTriplesMap, input, 
                        parameters, exeTriplesMap);                
                
            } //Conditions
            else {
                log.debug("Referencing Object Map with Logical Source with conditions.");
                //Build a join map where
                //  key: the parent expression
                //  value: the value extracted from the child
               boolean result = process_sameLS_withJC(node, performer, processor, subject, 
                        predicate, dataset, input, parentTriplesMap, 
                        joinConditions, exeTriplesMap, referencingObjectMap);
                if (!result) {
                    log.debug("Check for falllback object maps" );
                    Set<ReferencingObjectMap> fallbackReferencingObjectMaps =
                            referencingObjectMap.getFallbackReferencingObjectMaps();
                    log.debug("Found " + fallbackReferencingObjectMaps
                            + " fallback Referencing Object Maps");
                    //Process the joins first
                    if (fallbackReferencingObjectMaps.size() > 0) {
                        ObjectMapProcessor predicateObjectProcessor =
                                new StdObjectMapProcessor(map, processor);
                        predicateObjectProcessor.processPredicateObjectMap_RefObjMap(
                                dataset, subject, predicate, fallbackReferencingObjectMaps, node,
                                map, parameters, exeTriplesMap);
                    }
                }
            }
        }
    }
       
    private boolean processConditions(ReferencingObjectMap referencingObjectMap, 
            Object node, Set<Condition> conditions) {
        Map<String, String> parameters = null;
        Set<BindingCondition> bindingConditions = new HashSet<BindingCondition>();
        //Retrieving Conditions
        log.debug("Condition Referencing Object Map");
        ConditionReferencingObjectMap condRefObjMap =
                (ConditionReferencingObjectMap) referencingObjectMap;
        conditions = condRefObjMap.getConditions();

        //Processing conditions
        ConditionProcessor condProcessor = new StdConditionProcessor();
        boolean result = condProcessor.processConditions(
                node, termMapProcessor, conditions);

        //Processing Binding Conditions
        log.debug("Processing Conditions...");
        for (Condition condition : conditions) {

            if (condition.getClass().getSimpleName().equals("StdBindingCondition")) {
                BindingCondition bindCondition = (BindingCondition) condition;
                bindingConditions.add(bindCondition);
            }
        }
        parameters = processBindingConditions(node, bindingConditions);

        //If conditions fail  do not proceed
        if (!result && bindingConditions.isEmpty()) // && joinConditions.isEmpty())
        {
            return false;
        }
        else
            return true;
        
        //Binding Referencing Object Map
            /*if(referencingObjectMap.getClass().getSimpleName().
                    equals("BindingReferencingObjectMap")){
                log.debug("Processing Referencing Object Map "
                        + "with Binding Condition..");
                ConditionReferencingObjectMap bindingReferencingObjectMap = 
                        (ConditionReferencingObjectMap) referencingObjectMap;

            }*/
    }
    
    //TODO: Check the following two
    private void process_difLS_noJC_noBC(
            RMLPerformer performer, RMLProcessor processor,
            RMLDataset dataset, Resource subject, URI predicate, 
            TriplesMap parentTriplesMap, InputStream input, String[] exeTriplesMap) {
        log.debug("Referencing Object Map with Logical Source "
                + "without join and binding conditions.");
        performer = new JoinRMLPerformer(processor, subject, predicate);
        processor.execute(dataset, parentTriplesMap, performer, input,
                exeTriplesMap, false);
    }
    
    private boolean process_difLS_noJC_withBC(
            RMLPerformer performer, RMLProcessor processor,
            RMLDataset dataset, Resource subject, URI predicate,
            TriplesMap parentTriplesMap, InputStream input, String[] exeTriplesMap) {
        log.debug("Referencing Object Map with Logical Source "
                + "without join conditions but with bind conditions.");
        performer = new JoinRMLPerformer(processor, subject, predicate);
        processor.execute(dataset, parentTriplesMap, performer, input,
                exeTriplesMap, true);
        boolean status = processor.getIterationStatus();
        if (status == false) {
            return false;
        }
        else
            return true;
    }
    
    private void process_sameLS_noJC(
            RMLPerformer performer, RMLProcessor processor, RMLDataset dataset, 
            Object node, TriplesMap triplesMap, Resource subject, URI predicate,
            TriplesMap parentTriplesMap, InputStream input, 
            Map<String, String> parameters, String[] exeTriplesMap) {
        log.debug("Referencing Object Map with Logical Source without conditions.");
        performer = new SimpleReferencePerformer(processor, subject, predicate);

        if ((parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().
                equals("CSV"))
                || (parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().
                equals("XLSX"))
                || (parentTriplesMap.getLogicalSource().getIterator().
                equals(triplesMap.getLogicalSource().getIterator()))) {
            log.debug("Tabular-structured Referencing Object Map "
                    + "or Hierarchical-structured Referencing Object Map "
                    + "with the same iterator");
            performer.perform(
                    node, dataset, parentTriplesMap, exeTriplesMap, parameters, false);
        } else {
            log.debug("Hierarchical-structured Referencing Object Map");
            String expression = handleRelevantExpression(triplesMap, parentTriplesMap);

            processor.execute_node(
                    dataset, expression, parentTriplesMap,
                    performer, node, null, exeTriplesMap, false);
        }
    }
    
    public Map<String, String> processBindingConditions(
            Object node, Set<BindingCondition> bindingConditions){
        Map<String, String> parameters = new HashMap<String, String>();
        for(BindingCondition bindingCondition : bindingConditions){
            List<String> childValues = termMapProcessor.
                    extractValueFromNode(node, bindingCondition.getReference());
            log.debug("childValues " + childValues);
            for (String childValue : childValues) {    
                parameters.put(
                    bindingCondition.getVariable(), childValue);
            }
        }
        
        return parameters;
    }
    
    public boolean process_sameLS_withJC(Object node, RMLPerformer performer, 
            RMLProcessor processor, Resource subject, URI predicate, 
            RMLDataset dataset, InputStream input, TriplesMap parentTriplesMap, 
            Set<JoinCondition> joinConditions, String[] exeTriplesMap, 
            ReferencingObjectMap referencingObjectMap) {
        HashMap<String, String> joinMap = new HashMap<>();
        boolean result = true;
        log.debug("Processing " + joinConditions.size() 
                + " Referencing Object Map with Join Conditions...");

        for (JoinCondition joinCondition : joinConditions) {
            if(joinCondition.getChild() == null)
                continue;
            String child = joinCondition.getChild();
            List<String> childValues;
            if(child.contains("{")){
                be.ugent.mmlab.rml.input.processor.TemplateProcessor templateProcessor = 
                        new be.ugent.mmlab.rml.input.processor.TemplateProcessor();
                childValues = termMapProcessor.templateHandler(
                        child, node, parentTriplesMap.getLogicalSource().getReferenceFormulation(), null);
                log.debug("child template " + childValues);
            }
            else
                childValues = termMapProcessor.extractValueFromNode(
                    node, joinCondition.getChild());
            //Allow multiple values as child - 
            //fits with RML's definition of multiple Object Maps
            for (String childValue : childValues) { 
                joinMap.put(joinCondition.getParent(), childValue);
                if (joinMap.size() == joinConditions.size()) {
                    log.debug("Join Condition class " + 
                            joinCondition.getClass().getSimpleName());
                    if (joinCondition.getClass().getSimpleName().equals(
                            "StdJoinConditionMetric")) {
                        log.debug("Join Condition with Metric...");
                        StdJoinConditionMetric joinCondMetric =
                                (StdJoinConditionMetric) joinCondition;
                        Resource metric = joinCondMetric.getMetric();
                        performer = new ConditionalJoinRMLPerformer(
                                processor, joinMap, subject, predicate, metric);
                        processor.execute(dataset, parentTriplesMap, performer,
                                input, exeTriplesMap, false);
                    } else {
                        performer = new ConditionalJoinRMLPerformer(
                                processor, joinMap, subject, predicate);
                        log.debug("Join Condition without Metric...");
                        processor.execute(dataset, parentTriplesMap, performer,
                                        input, exeTriplesMap, false);
                        boolean status = processor.getIterationStatus();
                        log.debug("The current iteration status is " + status);
                        if (status == false) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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
